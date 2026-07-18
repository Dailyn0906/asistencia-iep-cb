# Informe de Pruebas de Software y Seguridad
## Sistema de Control de Asistencia — I.E.P. Cristina Beatriz

## 1. Pruebas de Software (Testing)

### 1.1 Tipo de pruebas aplicadas
Se implementaron **pruebas unitarias** con JUnit 5 y Mockito, aisladas de la
base de datos real mediante *mocks* de los repositorios (patrón DAO), lo que
permite ejecutar los tests de forma rápida y repetible.

### 1.2 Casos de prueba

| Clase probada | Caso de prueba | Qué valida |
|---|---|---|
| `AsistenciaServiceTest` | `guardar_creaNuevoRegistro_siNoExisteAsistenciaHoy` | Se crea un registro nuevo cuando el alumno no tiene asistencia registrada ese día |
| `AsistenciaServiceTest` | `guardar_actualizaRegistroExistente_enVezDeDuplicar` | **Regresión de un bug real encontrado**: antes, marcar asistencia dos veces al mismo alumno el mismo día generaba un registro duplicado en vez de actualizar el existente (violaba RF-02). Se corrigió el servicio y esta prueba evita que el bug regrese. |
| `AsistenciaServiceTest` | `contarFaltasPorAlumno_cuentaSoloLosEstadosFalta` | La lógica de conteo de faltas (usada para las alertas de RF-06) filtra correctamente por estado |

### 1.3 Cómo ejecutar las pruebas
```bash
mvn test
```

---

## 2. Pruebas de Seguridad

### 2.1 Hallazgo crítico corregido: credenciales hardcodeadas
**Antes de esta revisión**, `application.properties` tenía la contraseña de
MySQL escrita en texto plano (`admin123`) y **el archivo estaba en un
repositorio público de GitHub**, expuesta a cualquier persona.

**Corrección aplicada:** las credenciales ahora se leen desde variables de
entorno (`DB_URL`, `DB_USER`, `DB_PASSWORD`), con valores por defecto solo
para desarrollo local:
```properties
spring.datasource.password=${DB_PASSWORD:admin123}
```
**Recomendación pendiente:** rotar la contraseña real de MySQL, ya que estuvo
expuesta públicamente, y considerar limpiar el historial de Git si el
repositorio se mantiene público.

### 2.2 Autenticación y contraseñas
- Se verificó (test `UsuarioServiceSecurityTest`) que las contraseñas **nunca
  se almacenan en texto plano**: se encriptan con `BCryptPasswordEncoder`
  antes de guardarse.
- Se comprobó que BCrypt genera un *hash* distinto cada vez para la misma
  contraseña (gracias al *salt* aleatorio), lo que dificulta ataques de tabla
  arcoíris (*rainbow table*).

### 2.3 Inyección SQL
El acceso a datos se realiza exclusivamente mediante **Spring Data JPA /
Hibernate** con *named queries* derivadas de nombres de métodos
(`findByAulaIdAulaAndFecha`, etc.), nunca con concatenación manual de SQL.
Esto hace que las consultas sean **parametrizadas por diseño**, mitigando el
riesgo de inyección SQL (OWASP Top 10 - A03:2021).

### 2.4 Control de acceso por roles
**Hallazgo:** la configuración original (`SecurityConfig`) solo exigía estar
**autenticado** (`.anyRequest().authenticated()`), pero **no restringía qué
rol podía acceder a qué módulo**. Esto significaba que, por ejemplo, un
usuario con rol `DOCENTE` podía acceder al módulo de gestión de usuarios
(pensado solo para `ADMINISTRADOR`), contradiciendo directamente RF-07 y
RNF-04 del propio SRS.

**Corrección aplicada:** se agregaron reglas de autorización por ruta:

| Ruta | Roles permitidos | Requisito que cumple |
|---|---|---|
| `/asistencia/**` | DOCENTE | RF-01, RF-02, RF-05, RF-10 |
| `/reportes/**` | AUXILIAR, SECRETARIA, DIRECCION, ADMINISTRADOR | RF-04, RF-09 |
| `/usuarios/**` | ADMINISTRADOR | RF-07, RF-08 |
| `/actuator/**` | ADMINISTRADOR | Monitoreo restringido |
| resto de rutas | Cualquier usuario autenticado | CU-01 |

Además, el `dashboard.html` ahora oculta los módulos y enlaces que el rol
del usuario no puede usar (antes mostraba los tres módulos a todos, aunque
el clic terminara en un error de acceso denegado).

### 2.5 Expiración de sesión
Spring Security gestiona la expiración de sesión por inactividad de forma
predeterminada; se recomienda documentar explícitamente el tiempo configurado
en `SecurityConfig` para la sustentación oral.

### 2.6 Observaciones para siguientes iteraciones
- Agregar límite de intentos de login (ya contemplado en el CU-01 del SRS,
  pendiente de implementar en código).
- Agregar cabeceras de seguridad HTTP (`Content-Security-Policy`,
  `X-Frame-Options`) mediante Spring Security si se despliega en producción.
