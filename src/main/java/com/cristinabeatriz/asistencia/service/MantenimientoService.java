package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.repository.AsistenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio de mantenimiento del sistema.
 * Implementa tareas programadas (cron jobs) siguiendo las buenas prácticas
 * de mantenimiento: verificación periódica de integridad de datos y
 * recordatorios automáticos de respaldo de base de datos.
 *
 * El respaldo (backup) real de la base de datos se ejecuta con el script
 * externo scripts/backup.sh (o backup.bat en Windows), programado a nivel
 * de sistema operativo (cron / Programador de tareas). Ver
 * docs/PLAN_MANTENIMIENTO.md para el detalle completo.
 */
@Service
public class MantenimientoService {

    private static final Logger logger = LoggerFactory.getLogger(MantenimientoService.class);

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    /**
     * Tarea diaria de verificación del sistema.
     * Cron: segundo minuto hora díaDelMes mes díaDeLaSemana
     * "0 0 23 * * *" -> todos los días a las 23:00
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void verificacionDiaria() {
        logger.info("[MANTENIMIENTO] Iniciando verificación diaria del sistema...");
        long totalRegistros = asistenciaRepository.count();
        logger.info("[MANTENIMIENTO] Total de registros de asistencia en base de datos: {}", totalRegistros);
        logger.info("[MANTENIMIENTO] Verificación diaria finalizada correctamente.");
    }

    /**
     * Recordatorio automático de backup semanal.
     * Cron: "0 0 2 * * SUN" -> todos los domingos a las 2:00 a.m.
     * (horario de bajo uso del sistema, fuera del horario escolar).
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void recordatorioBackupSemanal() {
        logger.warn("[MANTENIMIENTO] Recordatorio: ejecutar scripts/backup.sh " +
                "para respaldar la base de datos asistencia_cb (backup semanal).");
    }
}
