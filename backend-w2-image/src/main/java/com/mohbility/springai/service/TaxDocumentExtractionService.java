package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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

    private final ChatClient chatClient;
    private final String extractionPrompt;

    public TaxDocumentExtractionService(
            ChatClient.Builder builder,
            @Value("${openai.tax-document-prompt-file}") Resource extractionPromptResource
    ) throws IOException {
        this.chatClient = builder.build();
        this.extractionPrompt = extractionPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public TaxDocumentResult extractFromFile(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();

        try {
            if (isPdf(file)) {
                List<byte[]> pages = convertPdfToImages(fileBytes);
                return chatClient.prompt()
                        .user(u -> {
                            u.text(extractionPrompt);
                            pages.forEach(page -> u.media(MimeTypeUtils.IMAGE_PNG, new ByteArrayResource(page)));
                        })
                        .call()
                        .entity(TaxDocumentResult.class);
            } else {
                String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";
                return chatClient.prompt()
                        .user(u -> u.text(extractionPrompt).media(MimeTypeUtils.parseMimeType(mimeType), new ByteArrayResource(fileBytes)))
                        .call()
                        .entity(TaxDocumentResult.class);
            }
        } catch (Exception e) {
            System.err.println("Error extracting tax document: " + e.getMessage());
            throw new RuntimeException("Failed to extract tax document. Please ensure the image is clear and shows a W-2 or 1099-NEC form. Error: " + e.getMessage(), e);
        }
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
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300, ImageType.RGB);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(image, "png", out);
                images.add(out.toByteArray());
            }
        }
        return images;
    }
}
