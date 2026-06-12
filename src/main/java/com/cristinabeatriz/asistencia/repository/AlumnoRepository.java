package com.cristinabeatriz.asistencia.repository;

import com.cristinabeatriz.asistencia.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    List<Alumno> findByAulaIdAula(Long idAula);
}