package com.cristinabeatriz.asistencia.dto;

import com.cristinabeatriz.asistencia.model.Aula;

/**
 * Resumen del estado de asistencia de un aula para el día actual.
 * Usado por el Panel en Tiempo Real del Auxiliar (RF-03, CU-04).
 */
public class ResumenAulaDTO {

    private final Aula aula;
    private final boolean registrado;
    private final int presentes;
    private final int faltas;
    private final int tardanzas;

    public ResumenAulaDTO(Aula aula, boolean registrado, int presentes, int faltas, int tardanzas) {
        this.aula = aula;
        this.registrado = registrado;
        this.presentes = presentes;
        this.faltas = faltas;
        this.tardanzas = tardanzas;
    }

    public Aula getAula() { return aula; }
    public boolean isRegistrado() { return registrado; }
    public int getPresentes() { return presentes; }
    public int getFaltas() { return faltas; }
    public int getTardanzas() { return tardanzas; }
}
