package com.cristinabeatriz.asistencia.security;

import com.cristinabeatriz.asistencia.model.Alumno;
import com.cristinabeatriz.asistencia.model.Aula;
import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.repository.AlumnoRepository;
import com.cristinabeatriz.asistencia.repository.AulaRepository;
import com.cristinabeatriz.asistencia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea datos de prueba al iniciar la aplicación (solo si no existen todavía),
 * para poder probar el sistema con los 5 roles y con aulas/alumnos reales.
 *
 * Usuarios de prueba (todos con contraseña: admin123):
 *   aylin        - DOCENTE
 *   auxiliar1    - AUXILIAR
 *   secretaria1  - SECRETARIA
 *   direccion1   - DIRECCION
 *   admin1       - ADMINISTRADOR
 */
@Component
public class InicializadorDatos implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Usuario docente = crearUsuarioSiNoExiste("aylin", "Aylin Damian Quispe", Usuario.RolEnum.DOCENTE);
        crearUsuarioSiNoExiste("auxiliar1", "María López (Auxiliar)", Usuario.RolEnum.AUXILIAR);
        crearUsuarioSiNoExiste("secretaria1", "Luisa Carmen (Secretaría)", Usuario.RolEnum.SECRETARIA);
        crearUsuarioSiNoExiste("direccion1", "Mariana Morales (Dirección)", Usuario.RolEnum.DIRECCION);
        crearUsuarioSiNoExiste("admin1", "María Gómez (Administrador)", Usuario.RolEnum.ADMINISTRADOR);

        if (aulaRepository.count() == 0 && docente != null) {
            Aula aula = new Aula();
            aula.setGrado("3° Primaria");
            aula.setSeccion('A');
            aula.setTutor(docente);
            aula = aulaRepository.save(aula);

            crearAlumno("Diego", "López García", "10456789", aula);
            crearAlumno("Valentina", "Cruz Mendoza", "10876543", aula);
            crearAlumno("José", "Álvarez Torres", "10544332", aula);
            crearAlumno("Camila", "Torres Quispe", "10999887", aula);

            System.out.println("Aula de prueba '3° Primaria A' creada con 4 alumnos");
        }
    }

    private Usuario crearUsuarioSiNoExiste(String username, String nombreCompleto, Usuario.RolEnum rol) {
        return usuarioRepository.findByUsername(username).orElseGet(() -> {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPasswordHash(passwordEncoder.encode("admin123"));
            usuario.setRol(rol);
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setEstado(true);
            Usuario guardado = usuarioRepository.save(usuario);
            System.out.println("Usuario de prueba creado: " + username + " (" + rol + ")");
            return guardado;
        });
    }

    private void crearAlumno(String nombres, String apellidos, String dni, Aula aula) {
        Alumno alumno = new Alumno();
        alumno.setNombres(nombres);
        alumno.setApellidos(apellidos);
        alumno.setDni(dni);
        alumno.setGrado(aula.getGrado());
        alumno.setSeccion(aula.getSeccion());
        alumno.setAula(aula);
        alumnoRepository.save(alumno);
    }
}