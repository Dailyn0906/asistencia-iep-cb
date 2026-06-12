package com.cristinabeatriz.asistencia.controller;

import com.cristinabeatriz.asistencia.model.Alumno;
import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.model.Aula;
import com.cristinabeatriz.asistencia.model.Usuario;
import com.cristinabeatriz.asistencia.service.AlumnoService;
import com.cristinabeatriz.asistencia.service.AsistenciaService;
import com.cristinabeatriz.asistencia.service.AulaService;
import com.cristinabeatriz.asistencia.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/asistencia")
public class AsistenciaController {

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verAsistencia(Model model, Authentication auth) {
        Optional<Usuario> usuario = usuarioService.findByUsername(auth.getName());
        List<Aula> aulas = aulaService.listarTodas();
        model.addAttribute("aulas", aulas);
        model.addAttribute("usuario", usuario.orElse(null));
        model.addAttribute("fechaHoy", LocalDate.now());
        return "asistencia";
    }

    @GetMapping("/aula/{idAula}")
    public String verAlumnosDeAula(@PathVariable Long idAula, Model model, Authentication auth) {
        Optional<Usuario> usuario = usuarioService.findByUsername(auth.getName());
        Optional<Aula> aula = aulaService.buscarPorId(idAula);
        if (aula.isEmpty()) return "redirect:/asistencia";

        List<Asistencia> asistencias = asistenciaService
                .listarPorAulaYFecha(idAula, LocalDate.now());

        model.addAttribute("aula", aula.get());
        model.addAttribute("alumnos", aula.get().getAlumnos());
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("usuario", usuario.orElse(null));
        model.addAttribute("fechaHoy", LocalDate.now());
        return "registro-asistencia";
    }

    @PostMapping("/guardar")
    public String guardarAsistencia(
            @RequestParam Long idAula,
            @RequestParam List<Long> idAlumnos,
            @RequestParam List<String> estados,
            Authentication auth) {

        Optional<Usuario> usuario = usuarioService.findByUsername(auth.getName());
        Optional<Aula> aula = aulaService.buscarPorId(idAula);

        if (usuario.isEmpty() || aula.isEmpty()) return "redirect:/asistencia";

        for (int i = 0; i < idAlumnos.size(); i++) {
            Alumno alumno = new Alumno();
            alumno.setIdAlumno(idAlumnos.get(i));

            Asistencia asistencia = new Asistencia();
            asistencia.setAlumno(alumno);
            asistencia.setAula(aula.get());
            asistencia.setRegistradoPor(usuario.get());
            asistencia.setEstado(Asistencia.EstadoAsistencia.valueOf(estados.get(i)));
            asistenciaService.guardar(asistencia);
        }
        return "redirect:/asistencia";
    }
}