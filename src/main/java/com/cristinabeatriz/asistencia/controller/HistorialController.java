package com.cristinabeatriz.asistencia.controller;

import com.cristinabeatriz.asistencia.model.Alumno;
import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.service.AlumnoService;
import com.cristinabeatriz.asistencia.service.AsistenciaService;
import com.cristinabeatriz.asistencia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

/**
 * Historial de asistencia por alumno (RF-09, CU-06).
 * Acceso: AUXILIAR, DIRECCION, ADMINISTRADOR (ver SecurityConfig).
 */
@Controller
@RequestMapping("/historial")
public class HistorialController {

    @Autowired
    private AlumnoService alumnoService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String buscar(@RequestParam(required = false) String query, Model model, Authentication auth) {
        Usuario usuario = usuarioService.findByUsername(auth.getName()).orElse(null);
        List<Alumno> resultados = (query != null && !query.isBlank())
                ? alumnoService.buscarPorNombre(query.trim())
                : List.of();

        model.addAttribute("usuario", usuario);
        model.addAttribute("query", query);
        model.addAttribute("resultados", resultados);
        return "historial";
    }

    @GetMapping("/{idAlumno}")
    public String verHistorial(@PathVariable Long idAlumno, Model model, Authentication auth) {
        Usuario usuario = usuarioService.findByUsername(auth.getName()).orElse(null);
        Alumno alumno = alumnoService.buscarPorId(idAlumno).orElse(null);

        if (alumno == null) {
            return "redirect:/historial";
        }

        List<Asistencia> asistencias = asistenciaService.listarPorAlumno(idAlumno);
        asistencias.sort(Comparator.comparing(Asistencia::getFecha).reversed());

        long totalFaltas = asistencias.stream().filter(Asistencia::esFalta).count();
        long totalTardanzas = asistencias.stream().filter(Asistencia::esTardanza).count();
        long totalPresentes = asistencias.size() - totalFaltas - totalTardanzas;

        model.addAttribute("usuario", usuario);
        model.addAttribute("alumno", alumno);
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("totalFaltas", totalFaltas);
        model.addAttribute("totalTardanzas", totalTardanzas);
        model.addAttribute("totalPresentes", totalPresentes);
        return "historial-detalle";
    }
}
