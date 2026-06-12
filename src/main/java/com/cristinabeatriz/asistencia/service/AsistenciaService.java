package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public Asistencia guardar(Asistencia asistencia) {
        asistencia.setFecha(LocalDate.now());
        asistencia.setHoraRegistro(LocalTime.now());
        return asistenciaRepository.save(asistencia);
    }

    public List<Asistencia> listarPorAulaYFecha(Long idAula, LocalDate fecha) {
        return asistenciaRepository.findByAulaIdAulaAndFecha(idAula, fecha);
    }

    public List<Asistencia> listarPorAlumno(Long idAlumno) {
        return asistenciaRepository.findByAlumnoIdAlumno(idAlumno);
    }

    public long contarFaltasPorAlumno(Long idAlumno) {
        return listarPorAlumno(idAlumno).stream()
                .filter(Asistencia::esFalta)
                .count();
    }
}