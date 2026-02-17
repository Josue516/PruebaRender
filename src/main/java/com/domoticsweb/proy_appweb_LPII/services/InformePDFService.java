package com.domoticsweb.proy_appweb_LPII.services;

import com.domoticsweb.proy_appweb_LPII.database.repositories.admin.AdminDashboardRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@AllArgsConstructor
public class InformePDFService {

    private final AdminDashboardRepository dashboardRepo;

    public byte[] generarInformeVentas7Dias() {
        try {
            // Crear documento PDF
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();

            // ========== ENCABEZADO ==========
            agregarEncabezado(document);
            
            // ========== RESUMEN EJECUTIVO ==========
            agregarResumenEjecutivo(document);
            
            // ========== TABLA DE VENTAS ==========
            agregarTablaVentas(document);
            
            // ========== PIE DE PÁGINA ==========
            agregarPiePagina(document);

            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }
    }

    private void agregarEncabezado(Document document) throws DocumentException {
        // Título principal
        Font tituloFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(31, 78, 121));
        Paragraph titulo = new Paragraph("IEoDomoTics", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        // Subtítulo
        Font subtituloFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(52, 73, 94));
        Paragraph subtitulo = new Paragraph("Informe de Ventas - Últimos 7 Días", subtituloFont);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(10);
        document.add(subtitulo);

        // Fecha de generación
        Font fechaFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Paragraph fecha = new Paragraph("Generado el: " + fechaActual, fechaFont);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        // Línea separadora
        LineSeparator linea = new LineSeparator();
        linea.setLineColor(new Color(52, 152, 219));
        document.add(new Chunk(linea));
        document.add(new Paragraph("\n"));
    }

    private void agregarResumenEjecutivo(Document document) throws DocumentException {
        // Obtener datos
        List<Map<String, Object>> ventas = dashboardRepo.ventasUltimos7DiasPorEstado();
        
        BigDecimal totalVentas = BigDecimal.ZERO;
        
        // Usar Set para contar días únicos
        Set<String> diasUnicos = new HashSet<>();
        
        for (Map<String, Object> venta : ventas) {
            // Agregar día al Set (automáticamente elimina duplicados)
            diasUnicos.add(venta.get("dia").toString());
            
            // Sumar total
            Object total = venta.get("total");
            if (total instanceof BigDecimal bd) {
                totalVentas = totalVentas.add(bd);
            } else if (total != null) {
                totalVentas = totalVentas.add(new BigDecimal(total.toString()));
            }
        }
        
        int diasConVentas = diasUnicos.size(); // ✅ Ahora cuenta días únicos
        
        BigDecimal promedioDiario = diasConVentas > 0 
            ? totalVentas.divide(BigDecimal.valueOf(diasConVentas), 2, java.math.RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        // Título de sección
        Font tituloSeccion = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(52, 73, 94));
        Paragraph tituloResumen = new Paragraph("Resumen Ejecutivo", tituloSeccion);
        tituloResumen.setSpacingBefore(10);
        tituloResumen.setSpacingAfter(15);
        document.add(tituloResumen);

        // Crear tabla de métricas (3 columnas)
        PdfPTable tablaMetricas = new PdfPTable(3);
        tablaMetricas.setWidthPercentage(100);
        tablaMetricas.setSpacingAfter(20);

        // Estilo de celdas
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        Font valueFont = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(52, 73, 94));
        Font subFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY);

        // Métrica 1: Total Vendido
        PdfPCell celda1 = crearCeldaMetrica(
            "TOTAL VENDIDO", 
            "S/ " + totalVentas.setScale(2, java.math.RoundingMode.HALF_UP),
            "Últimos 7 días",
            new Color(46, 204, 113)
        );
        tablaMetricas.addCell(celda1);

        // Métrica 2: Días con Ventas
        PdfPCell celda2 = crearCeldaMetrica(
            "DÍAS CON VENTAS", 
            String.valueOf(diasConVentas),
            "De 7 días totales",
            new Color(52, 152, 219)
        );
        tablaMetricas.addCell(celda2);

        // Métrica 3: Promedio Diario
        PdfPCell celda3 = crearCeldaMetrica(
            "PROMEDIO DIARIO", 
            "S/ " + promedioDiario.setScale(2, java.math.RoundingMode.HALF_UP),
            "Por día",
            new Color(155, 89, 182)
        );
        tablaMetricas.addCell(celda3);

        document.add(tablaMetricas);
    }

    private PdfPCell crearCeldaMetrica(String label, String valor, String subtexto, Color color) {
        PdfPCell celda = new PdfPCell();
        celda.setPadding(15);
        celda.setBackgroundColor(color);
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Label
        Font labelFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        Paragraph pLabel = new Paragraph(label, labelFont);
        pLabel.setAlignment(Element.ALIGN_CENTER);

        // Valor
        Font valueFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.WHITE);
        Paragraph pValor = new Paragraph(valor, valueFont);
        pValor.setAlignment(Element.ALIGN_CENTER);

        // Subtexto
        Font subFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(230, 230, 230));
        Paragraph pSub = new Paragraph(subtexto, subFont);
        pSub.setAlignment(Element.ALIGN_CENTER);

        celda.addElement(pLabel);
        celda.addElement(pValor);
        celda.addElement(pSub);

        return celda;
    }

    private void agregarTablaVentas(Document document) throws DocumentException {
        // Título de sección
        Font tituloSeccion = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(52, 73, 94));
        Paragraph tituloTabla = new Paragraph("Detalle de Ventas por Día y Estado", tituloSeccion);
        tituloTabla.setSpacingBefore(10);
        tituloTabla.setSpacingAfter(15);
        document.add(tituloTabla);

        // Obtener datos CON ESTADOS
        List<Map<String, Object>> ventas = dashboardRepo.ventasUltimos7DiasPorEstado();

        // Crear tabla (3 columnas: Fecha, Estado y Total)
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{2, 2, 1.5f});
        tabla.setSpacingAfter(20);

        // Encabezados
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
        
        PdfPCell headerFecha = new PdfPCell(new Phrase("Fecha", headerFont));
        headerFecha.setBackgroundColor(new Color(52, 73, 94));
        headerFecha.setPadding(10);
        headerFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(headerFecha);

        PdfPCell headerEstado = new PdfPCell(new Phrase("Estado", headerFont));
        headerEstado.setBackgroundColor(new Color(52, 73, 94));
        headerEstado.setPadding(10);
        headerEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(headerEstado);

        PdfPCell headerTotal = new PdfPCell(new Phrase("Total (S/)", headerFont));
        headerTotal.setBackgroundColor(new Color(52, 73, 94));
        headerTotal.setPadding(10);
        headerTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(headerTotal);

        // Datos
        Font dataFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font estadoFont = new Font(Font.HELVETICA, 9, Font.BOLD, Color.BLACK);
        BigDecimal sumaTotal = BigDecimal.ZERO;

        for (Map<String, Object> venta : ventas) {
            String dia = venta.get("dia").toString();
            String estado = venta.get("estado").toString();
            Object totalObj = venta.get("total");
            BigDecimal total = totalObj instanceof BigDecimal bd 
                ? bd 
                : new BigDecimal(totalObj.toString());

            sumaTotal = sumaTotal.add(total);

            // Celda Fecha
            PdfPCell celdaFecha = new PdfPCell(new Phrase(dia, dataFont));
            celdaFecha.setPadding(8);
            celdaFecha.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(celdaFecha);

            // Celda Estado con color
            PdfPCell celdaEstado = new PdfPCell(new Phrase(estado, estadoFont));
            celdaEstado.setPadding(8);
            celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            // Colorear según estado
            if (estado.equals("PAGADO")) {
                celdaEstado.setBackgroundColor(new Color(255, 243, 205)); // Amarillo claro
            } else if (estado.equals("EN_PREPARACION")) {
                celdaEstado.setBackgroundColor(new Color(207, 226, 243)); // Azul claro
            } else if (estado.equals("ENVIADO")) {
                celdaEstado.setBackgroundColor(new Color(225, 213, 231)); // Púrpura claro
            } else if (estado.equals("ENTREGADO")) {
                celdaEstado.setBackgroundColor(new Color(209, 236, 241)); // Verde claro
            }
            
            tabla.addCell(celdaEstado);

            // Celda Total
            PdfPCell celdaTotal = new PdfPCell(new Phrase(total.setScale(2, java.math.RoundingMode.HALF_UP).toString(), dataFont));
            celdaTotal.setPadding(8);
            celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tabla.addCell(celdaTotal);
        }

        // Fila de TOTAL (ocupa 3 columnas)
        Font totalFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
        
        PdfPCell celdaTotalLabel = new PdfPCell(new Phrase("TOTAL GENERAL", totalFont));
        celdaTotalLabel.setBackgroundColor(new Color(46, 204, 113));
        celdaTotalLabel.setPadding(10);
        celdaTotalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);
        celdaTotalLabel.setColspan(2); // Ocupa 2 columnas
        tabla.addCell(celdaTotalLabel);

        PdfPCell celdaTotalValor = new PdfPCell(new Phrase(sumaTotal.setScale(2, java.math.RoundingMode.HALF_UP).toString(), totalFont));
        celdaTotalValor.setBackgroundColor(new Color(46, 204, 113));
        celdaTotalValor.setPadding(10);
        celdaTotalValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabla.addCell(celdaTotalValor);

        document.add(tabla);
    }

    private void agregarPiePagina(Document document) throws DocumentException {
        // Línea separadora
        LineSeparator linea = new LineSeparator();
        linea.setLineColor(new Color(189, 195, 199));
        document.add(new Chunk(linea));
        document.add(new Paragraph("\n"));

        // Texto del pie
        Font pieFont = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);
        Paragraph pie = new Paragraph(
            "Este informe fue generado automáticamente por el sistema de gestión IEoDomoTics.\n" +
            "Para más información, contacte al administrador del sistema.",
            pieFont
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        document.add(pie);
    }
}
