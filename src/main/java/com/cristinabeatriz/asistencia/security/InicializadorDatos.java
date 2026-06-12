package com.cristinabeatriz.asistencia.security;

import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InicializadorDatos implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByUsername("aylin").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("aylin");
            usuario.setPasswordHash(passwordEncoder.encode("admin123"));
            usuario.setRol(Usuario.RolEnum.DOCENTE);
            usuario.setNombreCompleto("Aylin Damian Quispe");
            usuario.setEstado(true);
            usuarioRepository.save(usuario);
            System.out.println("Usuario aylin creado correctamente");
        }
    }
}