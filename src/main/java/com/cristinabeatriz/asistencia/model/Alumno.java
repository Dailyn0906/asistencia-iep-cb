package com.cristinabeatriz.asistencia.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alumnos")
public class Alumno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlumno;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true)
    private String dni;

    @Column(nullable = false)
    private String grado;

    @Column(nullable = false)
    private Character seccion;

    @ManyToOne
    @JoinColumn(name = "id_aula")
    private Aula aula;

    public Long getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Long idAlumno) { this.idAlumno = idAlumno; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }

    public Character getSeccion() { return seccion; }
    public void setSeccion(Character seccion) { this.seccion = seccion; }

    public Aula getAula() { return aula; }
    public void setAula(Aula aula) { this.aula = aula; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
