package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario guardar(Usuario usuario) {
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void cambiarEstado(Long id, boolean activo) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setEstado(activo);
            usuarioRepository.save(u);
        });
    }

    public java.util.List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}