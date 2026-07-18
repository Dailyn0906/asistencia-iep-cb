# Plan de Monitoreo
## Sistema de Control de Asistencia — I.E.P. Cristina Beatriz

## 1. Objetivo
Detectar de forma temprana fallas de disponibilidad, errores de la
aplicación o degradación de rendimiento, para cumplir con RNF-02
(disponibilidad mínima del 95% en horario escolar).

## 2. Herramientas utilizadas

### 2.1 Logs — Logback
El sistema ya registra eventos con **Logback** (`logback-spring.xml`):
- Inicios de sesión (exitosos y fallidos)
- Generación de reportes
- Errores de procesamiento
- Tareas de mantenimiento programadas

Los logs se guardan en `logs/asistencia-cb.log` con **rotación diaria y
retención de 30 días**, evitando que el archivo crezca indefinidamente.

**Qué se revisa en los logs:**
| Nivel | Ejemplo | Acción esperada |
|---|---|---|
| ERROR | Falla al generar reporte Excel | Revisar inmediatamente, puede afectar a Secretaría |
| WARN | Login fallido repetido | Posible intento de acceso no autorizado |
| INFO | Registro de asistencia guardado | Operación normal, sin acción |

### 2.2 Health checks — Spring Boot Actuator
Se integró **Spring Boot Actuator**, que expone endpoints reales de salud del
sistema:

| Endpoint | Qué muestra |
|---|---|
| `/actuator/health` | Estado general del sistema y de la conexión a MySQL |
| `/actuator/info` | Versión y datos de la aplicación desplegada |
| `/actuator/metrics` | Métricas de JVM, memoria, peticiones HTTP, etc. |

Ejemplo de verificación manual (o automatizable con un *cron* que haga
`curl` cada 5 minutos y alerte si la respuesta no es `"status":"UP"`):
```bash
curl http://localhost:8080/actuator/health
```

### 2.3 Rendimiento (performance)
Se instrumentó la generación de reportes Excel (`ReporteService`) con
**Guava `Stopwatch`**, que mide y registra en los logs el tiempo real que
toma generar cada reporte. Esto permite detectar si el proceso empieza a
degradarse conforme crece el volumen de datos (por ejemplo, si un reporte
mensual empieza a tardar varios segundos).

## 3. Frecuencia de monitoreo propuesta
| Actividad | Frecuencia | Responsable |
|---|---|---|
| Revisión de `/actuator/health` | Diaria (inicio de jornada escolar) | Administrador del sistema |
| Revisión de logs de errores | Diaria | Administrador del sistema |
| Revisión de métricas de rendimiento | Semanal | Equipo de desarrollo |

## 4. Escalamiento ante incidentes
1. Si `/actuator/health` reporta `"DOWN"` → revisar conexión a MySQL primero.
2. Si hay errores repetidos de login → posible ataque de fuerza bruta,
   revisar IP de origen en logs.
3. Si un reporte tarda anormalmente (logs de Stopwatch) → revisar volumen de
   datos e índices de base de datos.
