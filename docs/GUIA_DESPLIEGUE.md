# Guía de Despliegue
## Sistema de Control de Asistencia — I.E.P. Cristina Beatriz

## 1. Requisitos previos
- Java 17 o superior
- Maven 3.8+ (o el wrapper `./mvnw` incluido en el proyecto)
- MySQL Server 8.0
- Puerto 8080 disponible (configurable)

## 2. Empaquetado con Maven
El proyecto se empaqueta como un `.jar` ejecutable independiente:
```bash
./mvnw clean package -DskipTests
```
Esto genera `target/asistencia-0.0.1-SNAPSHOT.jar`.

> Se usa `-DskipTests` solo para el empaquetado rápido de despliegue; en CI
> o antes de un release se debe correr `./mvnw clean package` sin omitir
> tests, para asegurar que las pruebas (`AsistenciaServiceTest`,
> `UsuarioServiceSecurityTest`) pasen antes de desplegar.

## 3. Configuración por variables de entorno
Antes de ejecutar en un servidor, se configuran las credenciales de base de
datos como variables de entorno (nunca hardcodeadas, ver Informe de Pruebas
de Seguridad):
```bash
export DB_URL=jdbc:mysql://<host-servidor>:3306/asistencia_cb
export DB_USER=asistencia_app
export DB_PASSWORD=<contraseña-segura>
```

## 4. Ejecución
### 4.1 Local / servidor propio
```bash
java -jar target/asistencia-0.0.1-SNAPSHOT.jar
```
La aplicación queda disponible en `http://<host>:8080`.

### 4.2 Verificación post-despliegue
```bash
curl http://<host>:8080/actuator/health
```
Debe responder `{"status":"UP", ...}`. Si no responde, revisar:
1. Que MySQL esté accesible desde el servidor.
2. Que las variables de entorno estén correctamente configuradas.
3. Los logs en `logs/asistencia-cb.log`.

## 5. Base de datos
Al ejecutarse por primera vez, Hibernate crea automáticamente las tablas
(`spring.jpa.hibernate.ddl-auto=update`). Para producción se recomienda
cambiar esta propiedad a `validate` y manejar los cambios de esquema con
scripts SQL versionados, para evitar modificaciones accidentales de la
estructura de la base de datos.

## 6. Servidor de aplicaciones
El proyecto usa el servidor Tomcat embebido de Spring Boot (no requiere
instalar un Tomcat externo). Para exponerlo a internet de forma segura se
recomienda colocarlo detrás de un proxy inverso (Nginx/Apache) con HTTPS.

## 7. Evidencia de despliegue realizado

Para la sustentación del proyecto, la aplicación se desplegó y se expuso
públicamente usando **ngrok** (túnel seguro hacia el servidor local), lo que
permite acceder a la aplicación —corriendo con Java, Maven y MySQL— desde
cualquier dispositivo con conexión a internet, sin necesidad de contratar un
servicio de hosting de pago.

**Procedimiento realizado:**
1. Se empaquetó y ejecutó la aplicación localmente: `mvn spring-boot:run`
2. Se expuso el puerto 8080 con ngrok: `ngrok http 8080`
3. ngrok generó una URL pública HTTPS (ej. `https://divisive-feeble-urgency.ngrok-free.dev`)
   que redirige todo el tráfico hacia la aplicación Spring Boot corriendo
   localmente, incluyendo la conexión a la base de datos MySQL real.
4. Se verificó el acceso exitoso desde un dispositivo externo (fuera de la
   red local), confirmando que el despliegue es funcional de extremo a
   extremo: login, registro de asistencia, panel en tiempo real, reportes
   Excel, historial y gestión de usuarios.

**Nota:** esta es una solución de despliegue temporal apta para demostración
y pruebas colaborativas (el túnel requiere que el servidor local esté
encendido). Para un despliegue productivo permanente, se seguirían los pasos
de la sección 2-6 de esta guía en un servidor dedicado o servicio de
hosting en la nube.
