package com.cristinabeatriz.asistencia.repository;

import com.cristinabeatriz.asistencia.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByTutorIdUsuario(Long idUsuario);
}