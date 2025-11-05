package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaxDocumentService {

    private static final Logger log = LoggerFactory.getLogger(TaxDocumentService.class);
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
        log.info("TaxDocumentService.chat called with recipientIdentifier: {}", recipientIdentifier);
        
        if (recipientIdentifier == null || "undefined".equals(recipientIdentifier)) {
            recipientIdentifier = taxDocumentCacheService.getFirstRecipientName();
            log.info("recipientIdentifier was null, using first cached recipient: {}", recipientIdentifier);
            
            if (recipientIdentifier == null) {
                log.warn("No cached documents found");
                return "Please upload tax documents first to start the conversation.";
            }
        }
        
        List<TaxDocumentResult> documents = taxDocumentCacheService.get(recipientIdentifier);
        log.info("Retrieved {} documents from cache for recipientIdentifier: {}", 
                documents != null ? documents.size() : 0, recipientIdentifier);
        return taxDocumentChatService.chat(recipientIdentifier, documents, message);
    }

    public String generalChat(String message) {
        return generalChatService.chat(message);
    }

    public String generateSummary(String recipientIdentifier) {
        log.info("TaxDocumentService.generateSummary called with recipientIdentifier: {}", recipientIdentifier);
        
        if (recipientIdentifier == null || "undefined".equals(recipientIdentifier)) {
            recipientIdentifier = taxDocumentCacheService.getFirstRecipientName();
            log.info("recipientIdentifier was null for summary, using first cached recipient: {}", recipientIdentifier);
            
            if (recipientIdentifier == null) {
                log.warn("No cached documents found for summary");
                return "Please upload tax documents first.";
            }
        }
        
        List<TaxDocumentResult> documents = taxDocumentCacheService.get(recipientIdentifier);
        log.info("Retrieved {} documents from cache for summary, recipientIdentifier: {}", 
                documents != null ? documents.size() : 0, recipientIdentifier);
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
