package com.cristinabeatriz.asistencia.controller;

import com.cristinabeatriz.asistencia.model.Aula;
import com.cristinabeatriz.asistencia.service.AulaService;
import com.cristinabeatriz.asistencia.service.ReporteService;
import com.cristinabeatriz.asistencia.service.UsuarioService;
import com.cristinabeatriz.asistencia.model.Asistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private AulaService aulaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verReporters(Model model, Authentication auth) {
        List<Aula> aulas = aulaService.listarTodas();
        model.addAttribute("aulas", aulas);
        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("usuario", usuarioService.findByUsername(auth.getName()).orElse(null));
        return "reportes";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam Long idAula,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            logger.info("Exportando reporte para aula {} fecha {}", idAula, fecha);
            byte[] excel = reporteService.generarReporteExcel(idAula, fecha);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=asistencia-" + fecha + ".xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);
        } catch (IOException e) {
            logger.error("Error al generar reporte Excel: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/filtrar")
    public String filtrarReporters(
        @RequestParam Long idAula,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
        @RequestParam(required = false) String estado,
        Model model, Authentication auth) {

    logger.info("Filtrando reporters - aula: {}, fecha: {}", idAula, fecha);

    List<Asistencia> asistencias = reporteService
            .listarAsistencias(idAula, fecha, estado);

    List<Aula> aulas = aulaService.listarTodas();

    model.addAttribute("asistencias", asistencias);
    model.addAttribute("aulas", aulas);
    model.addAttribute("fechaHoy", fecha);
    model.addAttribute("aulaSeleccionada", idAula);
    model.addAttribute("fechaSeleccionada", fecha);
    model.addAttribute("usuario", usuarioService.findByUsername(auth.getName()).orElse(null));

    return "reportes";
}
}