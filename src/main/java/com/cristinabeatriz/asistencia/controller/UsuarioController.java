package com.cristinabeatriz.asistencia.controller;

import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Gestión de usuarios del sistema (RF-07, RF-08, CU-08).
 * Acceso restringido a ADMINISTRADOR en SecurityConfig.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("roles", Usuario.RolEnum.values());
        return "usuarios";
    }

    @PostMapping("/nuevo")
    public String crear(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String nombreCompleto,
            @RequestParam Usuario.RolEnum rol) {

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(password); // UsuarioService.guardar() lo encripta con BCrypt
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setRol(rol);
        usuario.setEstado(true);
        usuarioService.guardar(usuario);

        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id) {
        usuarioService.cambiarEstado(id, true);
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id) {
        usuarioService.cambiarEstado(id, false);
        return "redirect:/usuarios";
    }
}
