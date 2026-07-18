package com.cristinabeatriz.asistencia.repository;

import com.cristinabeatriz.asistencia.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByAulaIdAulaAndFecha(Long idAula, LocalDate fecha);
    List<Asistencia> findByAlumnoIdAlumno(Long idAlumno);
    Optional<Asistencia> findByAlumnoIdAlumnoAndFecha(Long idAlumno, LocalDate fecha);
}