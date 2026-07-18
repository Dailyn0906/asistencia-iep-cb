# Plan de Mantenimiento
## Sistema de Control de Asistencia — I.E.P. Cristina Beatriz

## 1. Objetivo
Garantizar la integridad de los datos (RNF-08) y la continuidad del servicio
mediante backups periódicos, tareas automatizadas y un procedimiento claro
de mantenimiento correctivo.

## 2. Backups (respaldos de base de datos)

### 2.1 Script de backup
Se creó `scripts/backup.sh` (Linux/Mac) y `scripts/backup.bat` (Windows), que:
1. Genera un dump completo de la base de datos `asistencia_cb` con `mysqldump`.
2. Comprime el archivo resultante (`.sql.gz`) para ahorrar espacio.
3. Elimina automáticamente backups con más de 30 días (política de retención).

### 2.2 Programación (cron job)
El script está pensado para ejecutarse automáticamente **cada domingo a las
2:00 a.m.**, fuera del horario escolar (RNF-02: 7:00 am – 5:00 pm), mediante
el *cron* del sistema operativo:
```cron
# crontab -e
0 2 * * 0 /ruta/al/proyecto/scripts/backup.sh >> /ruta/al/proyecto/logs/backup.log 2>&1
```
En Windows equivalente con el **Programador de tareas**, ejecutando
`scripts/backup.bat` semanalmente.

### 2.3 Tarea programada dentro de la aplicación
Además del backup a nivel de sistema operativo, se agregó `MantenimientoService`
con tareas `@Scheduled` (el equivalente de un cron job dentro de la app Java):

| Tarea | Cron | Qué hace |
|---|---|---|
| `verificacionDiaria()` | `0 0 23 * * *` (todos los días 11 pm) | Verifica el total de registros en BD y deja evidencia en los logs |
| `recordatorioBackupSemanal()` | `0 0 2 * * SUN` (domingos 2 am) | Registra en logs el recordatorio de ejecutar el backup |

## 3. Mantenimiento correctivo
| Situación | Procedimiento |
|---|---|
| Error reportado por un docente | Revisar `logs/asistencia-cb.log` del día y hora indicados |
| Base de datos corrupta o dañada | Restaurar el último backup: `mysql -u root -p asistencia_cb < backup.sql` |
| Actualización de dependencias | Ejecutar `mvn versions:display-dependency-updates` y probar en ambiente de desarrollo antes de producción |

## 4. Mantenimiento preventivo
- Revisión mensual de logs para identificar patrones de error recurrentes.
- Verificación trimestral de que los backups se pueden **restaurar
  correctamente** (no solo que se generan).
- Limpieza de logs antiguos (ya automatizada por la política de rotación de
  30 días de Logback).

## 5. Roles y responsables
| Rol | Responsabilidad |
|---|---|
| Administrador del sistema | Verificar ejecución de backups, revisar logs |
| Equipo de desarrollo | Aplicar actualizaciones de seguridad y corregir bugs reportados |
