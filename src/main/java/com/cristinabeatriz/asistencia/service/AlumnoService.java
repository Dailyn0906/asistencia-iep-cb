package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Alumno;
import com.cristinabeatriz.asistencia.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlumnoService {

    @Autowired
    private AlumnoRepository alumnoRepository;

    public List<Alumno> listarPorAula(Long idAula) {
        return alumnoRepository.findByAulaIdAula(idAula);
    }

    public Alumno guardar(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    public java.util.Optional<Alumno> buscarPorId(Long id) {
        return alumnoRepository.findById(id);
    }
}