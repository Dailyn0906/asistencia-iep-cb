package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Aula;
import com.cristinabeatriz.asistencia.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AulaService {

    @Autowired
    private AulaRepository aulaRepository;

    public List<Aula> listarTodas() {
        return aulaRepository.findAll();
    }

    public List<Aula> listarPorDocente(Long idUsuario) {
        return aulaRepository.findByTutorIdUsuario(idUsuario);
    }

    public Optional<Aula> buscarPorId(Long idAula) {
        return aulaRepository.findById(idAula);
    }

    public Aula guardar(Aula aula) {
        return aulaRepository.save(aula);
    }
}