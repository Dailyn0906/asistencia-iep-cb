package com.cristinabeatriz.asistencia.service;

import com.cristinabeatriz.asistencia.model.Asistencia;
import com.cristinabeatriz.asistencia.repository.AsistenciaRepository;
import com.google.common.base.Stopwatch;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public byte[] generarReporteExcel(Long idAula, LocalDate fecha) throws IOException {
        // Guava Stopwatch: mide el tiempo de generacion para el monitoreo de rendimiento
        Stopwatch cronometro = Stopwatch.createStarted();
        logger.info("Generando reporte Excel para aula {} en fecha {}", idAula, fecha);

        List<Asistencia> asistencias = asistenciaRepository
                .findByAulaIdAulaAndFecha(idAula, fecha);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Asistencia");

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row titulo = sheet.createRow(0);
        Cell tituloCell = titulo.createCell(0);
        tituloCell.setCellValue("I.E.P. Cristina Beatriz - Reporte de Asistencia");

        Row fechaRow = sheet.createRow(1);
        fechaRow.createCell(0).setCellValue("Fecha: " + fecha.toString());

        Row header = sheet.createRow(3);
        String[] columnas = {"N°", "Apellidos y Nombres", "DNI", "Grado", "Sección", "Estado", "Hora Registro"};
        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 4;
        int contador = 1;
        for (Asistencia a : asistencias) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contador++);
            row.createCell(1).setCellValue(a.getAlumno().getNombreCompleto());
            row.createCell(2).setCellValue(a.getAlumno().getDni());
            row.createCell(3).setCellValue(a.getAlumno().getGrado());
            row.createCell(4).setCellValue(String.valueOf(a.getAlumno().getSeccion()));
            row.createCell(5).setCellValue(a.getEstado().name());
            row.createCell(6).setCellValue(a.getHoraRegistro().toString());
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        cronometro.stop();
        logger.info("Reporte Excel generado con {} registros en {} ms",
                asistencias.size(), cronometro.elapsed(TimeUnit.MILLISECONDS));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
    public List<Asistencia> listarAsistencias(Long idAula, LocalDate fecha, String estado) {
    logger.info("Listando asistencias - aula: {}, fecha: {}", idAula, fecha);
    List<Asistencia> asistencias = asistenciaRepository
            .findByAulaIdAulaAndFecha(idAula, fecha);
    
    if (estado != null && !estado.isEmpty()) {
        asistencias = asistencias.stream()
                .filter(a -> a.getEstado().name().equals(estado))
                .collect(java.util.stream.Collectors.toList());
    }
    return asistencias;
    }
}