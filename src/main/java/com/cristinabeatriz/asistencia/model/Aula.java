package com.cristinabeatriz.asistencia.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "aulas")
public class Aula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAula;

    @Column(nullable = false)
    private String grado;

    @Column(nullable = false)
    private Character seccion;

    @ManyToOne
    @JoinColumn(name = "id_docente_tutor")
    private Usuario tutor;

    @OneToMany(mappedBy = "aula")
    private List<Alumno> alumnos;

    public Long getIdAula() { return idAula; }
    public void setIdAula(Long idAula) { this.idAula = idAula; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public Character getSeccion() { return seccion; }
    public void setSeccion(Character seccion) { this.seccion = seccion; }

    public Usuario getTutor() { return tutor; }
    public void setTutor(Usuario tutor) { this.tutor = tutor; }

    public List<Alumno> getAlumnos() { return alumnos; }
    public void setAlumnos(List<Alumno> alumnos) { this.alumnos = alumnos; }

    public int getTotalAlumnos() { return alumnos != null ? alumnos.size() : 0; }
}
