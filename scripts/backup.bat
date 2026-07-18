@echo off
REM ==========================================================================
REM Script de backup automatico - Sistema de Control de Asistencia
REM I.E.P. Cristina Beatriz
REM
REM Uso manual: backup.bat
REM Uso programado: Programador de tareas de Windows -> Semanal, domingo 2:00 a.m.
REM ==========================================================================

set DB_NAME=asistencia_cb
set DB_USER=root
set DB_PASSWORD=admin123
set BACKUP_DIR=%~dp0..\backups

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

for /f "tokens=2-4 delims=/ " %%a in ('date /t') do set FECHA=%%c-%%a-%%b
set ARCHIVO=%BACKUP_DIR%\asistencia_cb_%FECHA%.sql

echo Iniciando backup de %DB_NAME%...
mysqldump -u%DB_USER% -p%DB_PASSWORD% %DB_NAME% > "%ARCHIVO%"

if %ERRORLEVEL% EQU 0 (
    echo Backup completado: %ARCHIVO%
) else (
    echo ERROR: el backup fallo. Revisar credenciales/conexion a MySQL.
)
