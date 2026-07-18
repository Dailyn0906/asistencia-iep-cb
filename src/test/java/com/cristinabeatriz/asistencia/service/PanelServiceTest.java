package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.dto.ResumenAulaDTO;
import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.model.Aula;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PanelService (RF-03: panel en tiempo real del auxiliar).
 */
@ExtendWith(MockitoExtension.class)
class PanelServiceTest {

    @Mock
    private AulaService aulaService;

    @Mock
    private AsistenciaService asistenciaService;

    @InjectMocks
    private PanelService panelService;

    @Test
    void resumenDeHoy_marcaAulaComoRegistrada_siTieneAsistenciasHoy() {
        Aula aula = new Aula();
        aula.setIdAula(1L);
        aula.setGrado("3°");
        aula.setSeccion('A');

        Asistencia presente = new Asistencia();
        presente.setEstado(Asistencia.EstadoAsistencia.PRESENTE);
        Asistencia falta = new Asistencia();
        falta.setEstado(Asistencia.EstadoAsistencia.FALTA);

        when(aulaService.listarTodas()).thenReturn(List.of(aula));
        when(asistenciaService.listarPorAulaYFecha(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(presente, falta));

        List<ResumenAulaDTO> resumen = panelService.resumenDeHoy();

        assertEquals(1, resumen.size());
        assertTrue(resumen.get(0).isRegistrado());
        assertEquals(1, resumen.get(0).getPresentes());
        assertEquals(1, resumen.get(0).getFaltas());
    }

    @Test
    void resumenDeHoy_marcaAulaComoPendiente_siNoTieneAsistenciasHoy() {
        Aula aula = new Aula();
        aula.setIdAula(2L);

        when(aulaService.listarTodas()).thenReturn(List.of(aula));
        when(asistenciaService.listarPorAulaYFecha(eq(2L), any(LocalDate.class)))
                .thenReturn(List.of());

        List<ResumenAulaDTO> resumen = panelService.resumenDeHoy();

        assertFalse(resumen.get(0).isRegistrado());
        assertEquals(0, resumen.get(0).getPresentes());
    }
}
