#!/bin/bash
# ==========================================================================
# Script de backup automático - Sistema de Control de Asistencia
# I.E.P. Cristina Beatriz
#
# Uso manual:   ./backup.sh
# Uso con cron: 0 2 * * 0 /ruta/al/proyecto/scripts/backup.sh
#               (todos los domingos a las 2:00 a.m.)
# ==========================================================================

DB_NAME="asistencia_cb"
DB_USER="root"
DB_PASSWORD="${DB_PASSWORD:-admin123}"
BACKUP_DIR="$(dirname "$0")/../backups"
FECHA=$(date +"%Y-%m-%d_%H%M%S")
ARCHIVO_BACKUP="$BACKUP_DIR/asistencia_cb_$FECHA.sql"
DIAS_RETENCION=30

mkdir -p "$BACKUP_DIR"

echo "[$(date)] Iniciando backup de la base de datos $DB_NAME..."

mysqldump -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "$ARCHIVO_BACKUP"

if [ $? -eq 0 ]; then
    gzip "$ARCHIVO_BACKUP"
    echo "[$(date)] Backup completado exitosamente: $ARCHIVO_BACKUP.gz"
else
    echo "[$(date)] ERROR: el backup falló. Revisar credenciales/conexión a MySQL."
    exit 1
fi

# Elimina backups con más de $DIAS_RETENCION días (política de retención)
find "$BACKUP_DIR" -name "asistencia_cb_*.sql.gz" -mtime +$DIAS_RETENCION -delete
echo "[$(date)] Backups anteriores a $DIAS_RETENCION días eliminados (si existían)."
