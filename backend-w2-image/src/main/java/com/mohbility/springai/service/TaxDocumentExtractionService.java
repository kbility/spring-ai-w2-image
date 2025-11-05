package com.mohbility.springai.service;

import com.mohbility.springai.exception.TaxDocumentException;
import com.mohbility.springai.model.TaxDocumentResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaxDocumentExtractionService {

    private static final Logger log = LoggerFactory.getLogger(TaxDocumentExtractionService.class);
    private static final int PDF_DPI = 300;
    private static final String DEFAULT_MIME_TYPE = "image/jpeg";

    private final ChatClient chatClient;
    private final String extractionPrompt;

    public TaxDocumentExtractionService(
            ChatClient.Builder builder,
            @Value("${openai.tax-document-prompt-file}") Resource extractionPromptResource
    ) throws IOException {
        this.chatClient = builder.build();
        this.extractionPrompt = extractionPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public TaxDocumentResult extractFromFile(MultipartFile file) {
        validateFile(file);
        
        try {
            byte[] fileBytes = file.getBytes();
            log.info("Extracting tax document from file: {}", file.getOriginalFilename());

            TaxDocumentResult result = isPdf(file) 
                    ? extractFromPdf(fileBytes) 
                    : extractFromImage(fileBytes, file.getContentType());
            
            log.info("Successfully extracted {} document", result.getDocument_type());
            return result;
        } catch (IOException e) {
            log.error("IO error reading file: {}", file.getOriginalFilename(), e);
            throw new TaxDocumentException("Failed to read file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error extracting tax document from: {}", file.getOriginalFilename(), e);
            throw new TaxDocumentException("Failed to extract tax document. Please ensure the image is clear and shows a W-2 or 1099-NEC form.", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new TaxDocumentException("File is empty or null");
        }
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB
            throw new TaxDocumentException("File size exceeds 50MB limit");
        }
    }

    private TaxDocumentResult extractFromPdf(byte[] pdfBytes) throws Exception {
        List<byte[]> pages = convertPdfToImages(pdfBytes);
        return chatClient.prompt()
                .user(u -> {
                    u.text(extractionPrompt);
                    pages.forEach(page -> u.media(MimeTypeUtils.IMAGE_PNG, new ByteArrayResource(page)));
                })
                .call()
                .entity(TaxDocumentResult.class);
    }

    private TaxDocumentResult extractFromImage(byte[] imageBytes, String contentType) {
        String mimeType = contentType != null ? contentType : DEFAULT_MIME_TYPE;
        return chatClient.prompt()
                .user(u -> u.text(extractionPrompt)
                        .media(MimeTypeUtils.parseMimeType(mimeType), new ByteArrayResource(imageBytes)))
                .call()
                .entity(TaxDocumentResult.class);
    }

    private boolean isPdf(MultipartFile file) {
        String type = file.getContentType();
        String name = file.getOriginalFilename();
        return "application/pdf".equalsIgnoreCase(type) ||
                (name != null && name.toLowerCase().endsWith(".pdf"));
    }

    private List<byte[]> convertPdfToImages(byte[] pdfBytes) throws Exception {
        List<byte[]> images = new ArrayList<>();
        try (PDDocument doc = PDDocument.load(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            log.debug("Converting {} PDF pages to images", pageCount);
            
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, PDF_DPI, ImageType.RGB);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "png", out);
                images.add(out.toByteArray());
            }
        }
        return images;
    }
}
