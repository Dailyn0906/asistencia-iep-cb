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
        LocalDate hoy = LocalDate.now();

        // RF-02: si el alumno ya tiene un registro hoy, se actualiza en vez de duplicar
        Asistencia existente = asistenciaRepository
                .findByAlumnoIdAlumnoAndFecha(asistencia.getAlumno().getIdAlumno(), hoy)
                .orElse(null);

        if (existente != null) {
            existente.setEstado(asistencia.getEstado());
            existente.setRegistradoPor(asistencia.getRegistradoPor());
            existente.setHoraRegistro(LocalTime.now());
            return asistenciaRepository.save(existente);
        }

        asistencia.setFecha(hoy);
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