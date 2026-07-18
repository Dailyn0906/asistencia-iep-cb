package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de seguridad de UsuarioService.
 * Verifican que las contraseñas se almacenan encriptadas (BCrypt) y nunca
 * en texto plano, cumpliendo con RNF-04 (acceso protegido con autenticación).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceSecurityTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    // Se usa un spy sobre el encoder real (no un mock puro) para verificar el
    // comportamiento real de encriptación, no solo que "se llamó a un método".
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void guardar_encriptaLaContrasena_yNoLaGuardaEnTextoPlano() {
        String passwordOriginal = "miClaveSecreta123";

        Usuario usuario = new Usuario();
        usuario.setUsername("docente.prueba");
        usuario.setPasswordHash(passwordOriginal);

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario guardado = usuarioService.guardar(usuario);

        // La contraseña almacenada NUNCA debe ser igual al texto plano ingresado
        assertNotEquals(passwordOriginal, guardado.getPasswordHash());

        // Debe tener el formato típico de un hash BCrypt ($2a$ / $2b$ / $2y$)
        assertTrue(guardado.getPasswordHash().startsWith("$2"),
                "La contraseña no parece estar encriptada con BCrypt");
    }

    @Test
    void bcrypt_generaHashesDistintos_paraLaMismaContrasena() {
        // Buena práctica de seguridad: el salt hace que el mismo password
        // genere hashes distintos cada vez, dificultando ataques de tabla arcoíris.
        String hash1 = passwordEncoder.encode("claveIgual");
        String hash2 = passwordEncoder.encode("claveIgual");

        assertNotEquals(hash1, hash2);
        assertTrue(passwordEncoder.matches("claveIgual", hash1));
        assertTrue(passwordEncoder.matches("claveIgual", hash2));
    }
}
