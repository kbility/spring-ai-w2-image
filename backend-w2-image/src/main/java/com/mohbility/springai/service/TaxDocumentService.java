package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxDocumentService {

    private final TaxDocumentExtractionService taxDocumentExtractionService;
    private final TaxDocumentChatService taxDocumentChatService;
    private final GeneralChatService generalChatService;
    private final TaxDocumentCacheService taxDocumentCacheService;

    public TaxDocumentService(
            TaxDocumentExtractionService taxDocumentExtractionService,
            TaxDocumentChatService taxDocumentChatService,
            GeneralChatService generalChatService,
            TaxDocumentCacheService taxDocumentCacheService
    ) {
        this.taxDocumentExtractionService = taxDocumentExtractionService;
        this.taxDocumentChatService = taxDocumentChatService;
        this.generalChatService = generalChatService;
        this.taxDocumentCacheService = taxDocumentCacheService;
    }

    public TaxDocumentResult extractTaxDocumentFromFile(MultipartFile file) throws Exception {
        clearAllCache();
        TaxDocumentResult result = taxDocumentExtractionService.extractFromFile(file);
        taxDocumentCacheService.cache(result);
        return result;
    }

    public List<TaxDocumentResult> extractTaxDocumentsFromFiles(List<MultipartFile> files) throws Exception {
        clearAllCache();
        List<TaxDocumentResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            TaxDocumentResult result = taxDocumentExtractionService.extractFromFile(file);
            taxDocumentCacheService.cache(result);
            results.add(result);
        }
        return results;
    }

    public String chat(String recipientIdentifier, String message) {
        List<TaxDocumentResult> documents = taxDocumentCacheService.get(recipientIdentifier);
        return taxDocumentChatService.chat(recipientIdentifier, documents, message);
    }

    public String generalChat(String message) {
        return generalChatService.chat(message);
    }

    public String generateSummary(String recipientIdentifier) {
        List<TaxDocumentResult> documents = taxDocumentCacheService.get(recipientIdentifier);
        return taxDocumentChatService.generateSummary(recipientIdentifier, documents);
    }

    public String generateGeneralChatSummary() {
        return generalChatService.generateSummary();
    }

    private void clearAllCache() {
        taxDocumentCacheService.clear();
        taxDocumentChatService.clearHistory(null);
        generalChatService.clearHistory();
    }
}
