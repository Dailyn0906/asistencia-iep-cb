package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Alumno;
import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de AsistenciaService.
 * Cubren la regla de negocio RF-02 (un alumno no puede tener dos registros
 * de asistencia el mismo día) y el conteo de faltas usado para las alertas (RF-06).
 */
@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Alumno alumno;
    private Usuario docente;

    @BeforeEach
    void setUp() {
        alumno = new Alumno();
        alumno.setIdAlumno(1L);

        docente = new Usuario();
        docente.setIdUsuario(1L);
    }

    @Test
    void guardar_creaNuevoRegistro_siNoExisteAsistenciaHoy() {
        Asistencia nueva = new Asistencia();
        nueva.setAlumno(alumno);
        nueva.setRegistradoPor(docente);
        nueva.setEstado(Asistencia.EstadoAsistencia.PRESENTE);

        when(asistenciaRepository.findByAlumnoIdAlumnoAndFecha(eq(1L), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Asistencia resultado = asistenciaService.guardar(nueva);

        assertEquals(Asistencia.EstadoAsistencia.PRESENTE, resultado.getEstado());
        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void guardar_actualizaRegistroExistente_enVezDeDuplicar() {
        Asistencia existente = new Asistencia();
        existente.setIdAsistencia(99L);
        existente.setAlumno(alumno);
        existente.setEstado(Asistencia.EstadoAsistencia.PRESENTE);

        Asistencia nuevoIntento = new Asistencia();
        nuevoIntento.setAlumno(alumno);
        nuevoIntento.setRegistradoPor(docente);
        nuevoIntento.setEstado(Asistencia.EstadoAsistencia.TARDANZA);

        when(asistenciaRepository.findByAlumnoIdAlumnoAndFecha(eq(1L), any(LocalDate.class)))
                .thenReturn(Optional.of(existente));
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Asistencia resultado = asistenciaService.guardar(nuevoIntento);

        // Debe conservar el mismo id (actualizó, no creó uno nuevo)
        assertEquals(99L, resultado.getIdAsistencia());
        assertEquals(Asistencia.EstadoAsistencia.TARDANZA, resultado.getEstado());
        verify(asistenciaRepository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void contarFaltasPorAlumno_cuentaSoloLosEstadosFalta() {
        Asistencia falta1 = new Asistencia();
        falta1.setEstado(Asistencia.EstadoAsistencia.FALTA);

        Asistencia presente = new Asistencia();
        presente.setEstado(Asistencia.EstadoAsistencia.PRESENTE);

        Asistencia falta2 = new Asistencia();
        falta2.setEstado(Asistencia.EstadoAsistencia.FALTA);

        when(asistenciaRepository.findByAlumnoIdAlumno(1L))
                .thenReturn(List.of(falta1, presente, falta2));

        long total = asistenciaService.contarFaltasPorAlumno(1L);

        assertEquals(2, total);
    }
}
