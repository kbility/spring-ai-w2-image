package com.mohbility.springai.controller;

import com.mohbility.springai.model.TaxDocumentResult;
import com.mohbility.springai.model.AnalysisRequest;
import com.mohbility.springai.service.TaxDocumentService;
import com.mohbility.springai.utils.TaxDocumentExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class W2Controller {

    private final TaxDocumentService taxDocumentService;
    private List<TaxDocumentResult> lastTaxDocuments;

    public W2Controller(TaxDocumentService taxDocumentService) {
        this.taxDocumentService = taxDocumentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        TaxDocumentResult result = taxDocumentService.extractTaxDocumentFromFile(file);
        System.out.println(result);
        lastTaxDocuments = List.of(result);

        Map<String, Object> response = new HashMap<>();
        response.put("table", TaxDocumentExcelExporter.toTable(lastTaxDocuments));
        response.put("previews", List.of(toPreview(file)));
        response.put("refresh", true);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-multi")
    public ResponseEntity<Map<String, Object>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) throws Exception {
        List<TaxDocumentResult> results = taxDocumentService.extractTaxDocumentsFromFiles(files);
        lastTaxDocuments = results;

        List<String> previews = new ArrayList<>();
        for (MultipartFile f : files) {
            previews.add(toPreview(f));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("table", TaxDocumentExcelExporter.toTable(results));
        response.put("previews", previews);
        response.put("refresh", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response) throws Exception {
        if (lastTaxDocuments == null) return;
        byte[] excel = TaxDocumentExcelExporter.toExcel(lastTaxDocuments);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=tax_documents.xlsx");
        response.getOutputStream().write(excel);
    }

    private String toPreview(MultipartFile file) throws Exception {
        String type = file.getContentType();
        if (type == null) type = "application/octet-stream";

        if (type.startsWith("image/")) {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            return "data:" + type + ";base64," + base64;
        }

        String originalName = file.getOriginalFilename();
        boolean isPdf = "application/pdf".equalsIgnoreCase(type)
                || (originalName != null && originalName.toLowerCase().endsWith(".pdf"));
        if (isPdf) {
            byte[] pdfBytes = file.getBytes();
            try (PDDocument doc = PDDocument.load(pdfBytes)) {
                PDFRenderer renderer = new PDFRenderer(doc);
                BufferedImage image = renderer.renderImageWithDPI(0, 180, ImageType.RGB);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "png", out);
                String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
                return "data:image/png;base64," + base64;
            }
        }

        return "/images/pdf-icon.png";
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyze(@RequestBody AnalysisRequest request) {
        String answer = taxDocumentService.chat(request.getEmployeeName(), request.getQuestion());
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/{employeeName}")
    public ResponseEntity<Map<String, String>> getSummary(@PathVariable String employeeName) {
        String summary = taxDocumentService.generateSummary(employeeName);
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat/general")
    public ResponseEntity<Map<String, String>> generalChat(@RequestBody AnalysisRequest request) {
        String answer = taxDocumentService.generalChat(request.getQuestion());
        Map<String, String> response = new HashMap<>();
        response.put("answer", answer);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chat/general/summary")
    public ResponseEntity<Map<String, String>> getGeneralChatSummary() {
        String summary = taxDocumentService.generateGeneralChatSummary();
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        return ResponseEntity.ok(response);
    }
}
