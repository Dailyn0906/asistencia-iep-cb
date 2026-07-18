package com.cristinabeatriz.asistencia.controller;

import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.service.PanelService;
import com.cristinabeatriz.asistencia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

/**
 * Panel de asistencia en tiempo real (RF-03, CU-04).
 * Acceso: AUXILIAR, DIRECCION, ADMINISTRADOR (ver SecurityConfig).
 */
@Controller
@RequestMapping("/panel")
public class PanelController {

    @Autowired
    private PanelService panelService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/tiempo-real")
    public String verPanel(Model model, Authentication auth) {
        Usuario usuario = usuarioService.findByUsername(auth.getName()).orElse(null);
        var resumen = panelService.resumenDeHoy();

        long aulasRegistradas = resumen.stream().filter(r -> r.isRegistrado()).count();
        int totalFaltas = resumen.stream().mapToInt(r -> r.getFaltas()).sum();
        int totalTardanzas = resumen.stream().mapToInt(r -> r.getTardanzas()).sum();
        long aulasPendientes = resumen.size() - aulasRegistradas;

        model.addAttribute("usuario", usuario);
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("resumen", resumen);
        model.addAttribute("totalAulas", resumen.size());
        model.addAttribute("aulasRegistradas", aulasRegistradas);
        model.addAttribute("aulasPendientes", aulasPendientes);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalTardanzas", totalTardanzas);
        return "panel-auxiliar";
    }
}
