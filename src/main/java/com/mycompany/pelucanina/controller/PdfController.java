package com.mycompany.pelucanina.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.mycompany.pelucanina.model.HistorialClinico;
import com.mycompany.pelucanina.model.Mascota;
import com.mycompany.pelucanina.model.Vacuna;
import com.mycompany.pelucanina.service.HistorialClinicoService;
import com.mycompany.pelucanina.service.MascotaService;
import com.mycompany.pelucanina.service.VacunaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class PdfController {

    private final MascotaService mascotaService;
    private final HistorialClinicoService historialService;
    private final VacunaService vacunaService;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Colores
    private static final BaseColor COLOR_PRIMARIO = new BaseColor(102, 126, 234);
    private static final BaseColor COLOR_SECUNDARIO = new BaseColor(118, 75, 162);
    private static final BaseColor COLOR_GRIS = new BaseColor(248, 249, 250);
    private static final BaseColor COLOR_TEXTO = new BaseColor(51, 51, 51);
    private static final BaseColor COLOR_SUBTEXTO = new BaseColor(136, 136, 136);
    private static final BaseColor COLOR_ROJO = new BaseColor(220, 53, 69);

    public PdfController(MascotaService mascotaService,
                         HistorialClinicoService historialService,
                         VacunaService vacunaService) {
        this.mascotaService = mascotaService;
        this.historialService = historialService;
        this.vacunaService = vacunaService;
    }

    @GetMapping("/pdf/mascota/{id}")
    public void generarPdf(@PathVariable Integer id,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) throws IOException {

        Mascota mascota = mascotaService.obtenerMascotaPorId(id).orElse(null);
        if (mascota == null) {
            response.sendRedirect("/mascotas");
            return;
        }

        List<HistorialClinico> historial = historialService.obtenerPorMascota(id);
        List<Vacuna> vacunas = vacunaService.obtenerPorMascota(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"ficha-" + mascota.getNombre().toLowerCase() + ".pdf\"");

        try {
            Document doc = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            agregarEncabezado(doc, mascota);
            agregarDatosMascota(doc, mascota);
            agregarDatosDuenio(doc, mascota);
            agregarHistorial(doc, historial);
            agregarVacunas(doc, vacunas);

            doc.close();
        } catch (DocumentException e) {
            response.sendRedirect("/mascotas");
        }
    }

    private void agregarEncabezado(Document doc, Mascota mascota) throws DocumentException {
        // Barra de título
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_PRIMARIO);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(15);

        Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE);
        Font fuenteSubtitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.WHITE);

        Paragraph titulo = new Paragraph("🐾 Servicio Integral para Mascotas\n", fuenteTitulo);
        titulo.add(new Chunk("Ficha Clínica — " + mascota.getNombre(), fuenteSubtitulo));
        titulo.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(titulo);
        header.addCell(cell);
        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    private void agregarDatosMascota(Document doc, Mascota mascota) throws DocumentException {
        agregarTituloSeccion(doc, "Datos de la Mascota");

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(10);

        agregarFilaTabla(tabla, "Nombre", mascota.getNombre());
        agregarFilaTabla(tabla, "Especie", mascota.getEspecie() != null ? mascota.getEspecie() : "-");
        agregarFilaTabla(tabla, "Color", mascota.getColor() != null ? mascota.getColor() : "-");
        agregarFilaTabla(tabla, "Fecha de Nacimiento",
                mascota.getFechaNacimiento() != null ? mascota.getFechaNacimiento().format(FORMATO_FECHA) : "-");
        agregarFilaTabla(tabla, "Alérgico", mascota.getAlergico() != null ? mascota.getAlergico() : "-");
        agregarFilaTabla(tabla, "Atención Especial", mascota.getAtencionEspecial() != null ? mascota.getAtencionEspecial() : "-");
        if (mascota.getObservaciones() != null && !mascota.getObservaciones().isEmpty()) {
            agregarFilaTabla(tabla, "Observaciones", mascota.getObservaciones());
        }

        doc.add(tabla);
    }

    private void agregarDatosDuenio(Document doc, Mascota mascota) throws DocumentException {
        if (mascota.getUnduenio() == null) return;

        agregarTituloSeccion(doc, "Datos del Dueño");

        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(10);

        agregarFilaTabla(tabla, "Nombre", mascota.getUnduenio().getNombre());
        agregarFilaTabla(tabla, "Celular",
                mascota.getUnduenio().getCelDuenio() != null ? mascota.getUnduenio().getCelDuenio() : "-");
        agregarFilaTabla(tabla, "Dirección",
                mascota.getUnduenio().getDireccion() != null ? mascota.getUnduenio().getDireccion() : "-");
        if (mascota.getUnduenio().getCodigoPostal() != null) {
            agregarFilaTabla(tabla, "Código Postal", mascota.getUnduenio().getCodigoPostal());
        }

        doc.add(tabla);
    }

    private void agregarHistorial(Document doc, List<HistorialClinico> historial) throws DocumentException {
        agregarTituloSeccion(doc, "Historial Clínico (" + historial.size() + " consultas)");

        if (historial.isEmpty()) {
            agregarTextoVacio(doc, "No hay consultas registradas");
            return;
        }

        for (HistorialClinico h : historial) {
            PdfPTable card = new PdfPTable(1);
            card.setWidthPercentage(100);
            card.setSpacingAfter(8);

            // Header de la consulta
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(COLOR_PRIMARIO);
            headerCell.setBorder(Rectangle.NO_BORDER);
            headerCell.setPadding(8);
            Font fuenteHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            String fechaStr = h.getFechaConsulta() != null ? h.getFechaConsulta().format(FORMATO_FECHA) : "-";
            String vetStr = h.getVeterinario() != null ? " — " + h.getVeterinario().getNombreCompleto() : "";
            headerCell.addElement(new Paragraph("Consulta: " + fechaStr + vetStr, fuenteHeader));
            card.addCell(headerCell);

            // Contenido
            PdfPCell contentCell = new PdfPCell();
            contentCell.setBackgroundColor(COLOR_GRIS);
            contentCell.setBorder(Rectangle.NO_BORDER);
            contentCell.setPadding(10);

            agregarCampoEnCelda(contentCell, "Motivo", h.getMotivoConsulta());
            agregarCampoEnCelda(contentCell, "Diagnóstico", h.getDiagnostico());
            agregarCampoEnCelda(contentCell, "Tratamiento", h.getTratamiento());
            agregarCampoEnCelda(contentCell, "Medicamentos", h.getMedicamentos());
            if (h.getPeso() != null) {
                agregarCampoEnCelda(contentCell, "Peso", h.getPeso() + " kg");
            }
            if (h.getProximoControl() != null) {
                agregarCampoEnCelda(contentCell, "Próximo Control", h.getProximoControl().format(FORMATO_FECHA));
            }

            card.addCell(contentCell);
            doc.add(card);
        }
    }

    private void agregarVacunas(Document doc, List<Vacuna> vacunas) throws DocumentException {
        agregarTituloSeccion(doc, "Vacunas (" + vacunas.size() + " registradas)");

        if (vacunas.isEmpty()) {
            agregarTextoVacio(doc, "No hay vacunas registradas");
            return;
        }

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(10);
        try {
            tabla.setWidths(new float[]{3, 2, 2, 3});
        } catch (DocumentException ignored) {}

        // Encabezados
        agregarCeldaEncabezado(tabla, "Vacuna");
        agregarCeldaEncabezado(tabla, "Aplicada");
        agregarCeldaEncabezado(tabla, "Próxima Dosis");
        agregarCeldaEncabezado(tabla, "Veterinario");

        for (Vacuna v : vacunas) {
            boolean vencida = v.getProximaDosis() != null &&
                    v.getProximaDosis().isBefore(java.time.LocalDate.now());

            agregarCeldaDato(tabla, v.getNombre());
            agregarCeldaDato(tabla, v.getFechaAplicacion() != null ? v.getFechaAplicacion().format(FORMATO_FECHA) : "-");

            // Próxima dosis en rojo si vencida
            PdfPCell celdaDosis = new PdfPCell();
            celdaDosis.setBorder(Rectangle.BOTTOM);
            celdaDosis.setBorderColor(new BaseColor(220, 220, 220));
            celdaDosis.setPadding(6);
            Font fuenteDosis = new Font(Font.FontFamily.HELVETICA, 9,
                    vencida ? Font.BOLD : Font.NORMAL,
                    vencida ? COLOR_ROJO : COLOR_TEXTO);
            celdaDosis.addElement(new Paragraph(
                    v.getProximaDosis() != null ? v.getProximaDosis().format(FORMATO_FECHA) + (vencida ? " ⚠" : "") : "-",
                    fuenteDosis));
            tabla.addCell(celdaDosis);

            agregarCeldaDato(tabla, v.getVeterinario() != null ? v.getVeterinario().getNombreCompleto() : "-");
        }

        doc.add(tabla);
    }

    // --- Helpers ---

    private void agregarTituloSeccion(Document doc, String titulo) throws DocumentException {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, COLOR_PRIMARIO);
        Paragraph p = new Paragraph(titulo, fuente);
        p.setSpacingBefore(12);
        p.setSpacingAfter(6);
        doc.add(p);

        // Línea separadora
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_PRIMARIO);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(2);
        linea.addCell(cell);
        linea.setSpacingAfter(8);
        doc.add(linea);
    }

    private void agregarFilaTabla(PdfPTable tabla, String label, String valor) {
        Font fuenteLabel = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_SUBTEXTO);
        Font fuenteValor = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_TEXTO);

        PdfPCell celdaLabel = new PdfPCell(new Phrase(label, fuenteLabel));
        celdaLabel.setBorder(Rectangle.NO_BORDER);
        celdaLabel.setBackgroundColor(COLOR_GRIS);
        celdaLabel.setPadding(6);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor, fuenteValor));
        celdaValor.setBorder(Rectangle.NO_BORDER);
        celdaValor.setPadding(6);

        tabla.addCell(celdaLabel);
        tabla.addCell(celdaValor);
    }

    private void agregarCampoEnCelda(PdfPCell cell, String label, String valor) {
        if (valor == null || valor.isEmpty()) return;
        Font fuenteLabel = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, COLOR_SUBTEXTO);
        Font fuenteValor = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_TEXTO);
        cell.addElement(new Paragraph(label, fuenteLabel));
        cell.addElement(new Paragraph(valor + "\n", fuenteValor));
    }

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(COLOR_SECUNDARIO);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(7);
        tabla.addCell(cell);
    }

    private void agregarCeldaDato(PdfPTable tabla, String texto) {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_TEXTO);
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        cell.setPadding(6);
        tabla.addCell(cell);
    }

    private void agregarTextoVacio(Document doc, String texto) throws DocumentException {
        Font fuente = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, COLOR_SUBTEXTO);
        Paragraph p = new Paragraph(texto, fuente);
        p.setSpacingAfter(10);
        doc.add(p);
    }
}