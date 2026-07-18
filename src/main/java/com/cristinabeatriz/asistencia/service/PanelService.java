package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.dto.ResumenAulaDTO;
import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.model.Aula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel en tiempo real para el Auxiliar de Educación (RF-03, CU-04).
 * Consolida, por cada aula, si ya se registró asistencia hoy y sus totales.
 */
@Service
public class PanelService {

    @Autowired
    private AulaService aulaService;

    @Autowired
    private AsistenciaService asistenciaService;

    public List<ResumenAulaDTO> resumenDeHoy() {
        LocalDate hoy = LocalDate.now();
        List<Aula> aulas = aulaService.listarTodas();
        List<ResumenAulaDTO> resumen = new ArrayList<>();

        for (Aula aula : aulas) {
            List<Asistencia> asistencias = asistenciaService.listarPorAulaYFecha(aula.getIdAula(), hoy);

            long presentes = asistencias.stream()
                    .filter(a -> a.getEstado() == Asistencia.EstadoAsistencia.PRESENTE).count();
            long faltas = asistencias.stream().filter(Asistencia::esFalta).count();
            long tardanzas = asistencias.stream().filter(Asistencia::esTardanza).count();
            boolean registrado = !asistencias.isEmpty();

            resumen.add(new ResumenAulaDTO(aula, registrado, (int) presentes, (int) faltas, (int) tardanzas));
        }
        return resumen;
    }
}
