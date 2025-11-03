package com.mohbility.springai.controller;

import com.mohbility.springai.model.TaxDocumentResult;
import com.mohbility.springai.model.AnalysisRequest;
import com.mohbility.springai.service.TaxDocumentService;
import com.mohbility.springai.utils.TaxDocumentExcelExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping
public class TaxDocumentController {

    private final TaxDocumentService taxDocumentService;
    private final Map<String, List<TaxDocumentResult>> sessionDocuments = new ConcurrentHashMap<>();
    private static final String DEFAULT_SESSION = "default";

    public TaxDocumentController(TaxDocumentService taxDocumentService) {
        this.taxDocumentService = taxDocumentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            TaxDocumentResult result = taxDocumentService.extractTaxDocumentFromFile(file);
            List<TaxDocumentResult> documents = List.of(result);
            sessionDocuments.put(DEFAULT_SESSION, documents);

            Map<String, Object> response = Map.of(
                    "table", TaxDocumentExcelExporter.toTable(documents),
                    "previews", List.of(generatePreview(file)),
                    "refresh", true
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-multi")
    public ResponseEntity<?> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        try {
            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No files provided"));
            }

            List<TaxDocumentResult> results = taxDocumentService.extractTaxDocumentsFromFiles(files);
            sessionDocuments.put(DEFAULT_SESSION, results);

            List<String> previews = files.stream()
                    .map(this::generatePreviewSafe)
                    .collect(Collectors.toList());

            Map<String, Object> response = Map.of(
                    "table", TaxDocumentExcelExporter.toTable(results),
                    "previews", previews,
                    "refresh", true
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process files: " + e.getMessage()));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadExcel(HttpServletResponse response) {
        try {
            List<TaxDocumentResult> documents = sessionDocuments.get(DEFAULT_SESSION);
            if (documents == null || documents.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No documents available for download"));
            }

            byte[] excel = TaxDocumentExcelExporter.toExcel(documents);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=tax_documents.xlsx");
            response.getOutputStream().write(excel);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate Excel: " + e.getMessage()));
        }
    }

    private String generatePreview(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        if (contentType.startsWith("image/")) {
            return encodeImageToBase64(file.getBytes(), contentType);
        }

        if (isPdfFile(file, contentType)) {
            return convertPdfToPreview(file.getBytes());
        }

        return "/images/pdf-icon.png";
    }

    private String generatePreviewSafe(MultipartFile file) {
        try {
            return generatePreview(file);
        } catch (Exception e) {
            return "/images/pdf-icon.png";
        }
    }

    private boolean isPdfFile(MultipartFile file, String contentType) {
        if ("application/pdf".equalsIgnoreCase(contentType)) {
            return true;
        }
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".pdf");
    }

    private String encodeImageToBase64(byte[] imageBytes, String contentType) {
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + contentType + ";base64," + base64;
    }

    private String convertPdfToPreview(byte[] pdfBytes) throws IOException {
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage image = renderer.renderImageWithDPI(0, 180, ImageType.RGB);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return "data:image/png;base64," + base64;
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody AnalysisRequest request) {
        try {
            if (request.getEmployeeName() == null || request.getQuestion() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Employee name and question are required"));
            }

            String answer = taxDocumentService.chat(request.getEmployeeName(), request.getQuestion());
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to analyze: " + e.getMessage()));
        }
    }

    @GetMapping("/summary/{employeeName}")
    public ResponseEntity<?> getSummary(@PathVariable String employeeName) {
        try {
            String summary = taxDocumentService.generateSummary(employeeName);
            return ResponseEntity.ok(Map.of("summary", summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate summary: " + e.getMessage()));
        }
    }

    @PostMapping("/chat/general")
    public ResponseEntity<?> generalChat(@RequestBody AnalysisRequest request) {
        try {
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question is required"));
            }

            String answer = taxDocumentService.generalChat(request.getQuestion());
            return ResponseEntity.ok(Map.of("answer", answer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process chat: " + e.getMessage()));
        }
    }

    @GetMapping("/chat/general/summary")
    public ResponseEntity<?> getGeneralChatSummary() {
        try {
            String summary = taxDocumentService.generateGeneralChatSummary();
            return ResponseEntity.ok(Map.of("summary", summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate summary: " + e.getMessage()));
        }
    }
}
