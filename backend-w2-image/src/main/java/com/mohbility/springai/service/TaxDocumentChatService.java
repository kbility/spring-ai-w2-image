package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class TaxDocumentChatService {

    private static final Logger log = LoggerFactory.getLogger(TaxDocumentChatService.class);
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final String conversationPrompt;

    public TaxDocumentChatService(
            ChatClient.Builder builder,
            @Value("${openai.conversation-prompt-file}") Resource conversationPromptResource,
            ChatMemory regularChatMemory
    ) throws IOException {
        this.chatMemory = regularChatMemory;
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.conversationPrompt = conversationPromptResource.getContentAsString(StandardCharsets.UTF_8);
    }

    public String chat(String recipientIdentifier, List<TaxDocumentResult> documents, String message) {
        log.info("Chat called with recipientIdentifier: {}, documents size: {}, message: {}", 
                recipientIdentifier, documents != null ? documents.size() : 0, message);
        
        if (documents == null || documents.isEmpty()) {
            log.warn("No documents found for recipientIdentifier: {}", recipientIdentifier);
            return "Please upload tax documents first to start the conversation.";
        }

        String recipientName = documents.get(0).getRecipient_name() != null
                ? documents.get(0).getRecipient_name()
                : "Taxpayer";
        
        log.info("Recipient name: {}", recipientName);

        String systemPrompt = conversationPrompt
                .replace("{conversation_history}", "")
                .replace("{employee_name}", recipientName)
                .replace("{w2_count}", String.valueOf(documents.size()))
                .replace("{w2_details}", buildDocumentSummary(documents))
                .replace("{total_wages}", String.format("%.2f", calculateTotalIncome(documents)))
                .replace("{total_federal_tax}", String.format("%.2f", calculateTotalFederalTax(documents)))
                .replace("{total_ss_wages}", String.format("%.2f", calculateTotalSSWages(documents)))
                .replace("{total_medicare_wages}", String.format("%.2f", calculateTotalMedicareWages(documents)))
                .replace("{total_state_wages}", String.format("%.2f", calculateTotalStateIncome(documents)))
                .replace("{total_state_tax}", String.format("%.2f", calculateTotalStateTax(documents)))
                .replace("{additional_info}", "")
                .replace("{current_message}", "");

        try {
            log.info("Calling ChatClient with recipientIdentifier: {}", recipientIdentifier);
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .advisors(a -> a.param("chat_memory_conversation_id", recipientIdentifier))
                    .call()
                    .content();
            log.info("ChatClient response received successfully");
            return response;
        } catch (Exception e) {
            log.error("Error in chat method for recipientIdentifier: {}", recipientIdentifier, e);
            throw e;
        }
    }

    public String generateSummary(String recipientIdentifier, List<TaxDocumentResult> documents) {
        log.info("generateSummary called with recipientIdentifier: {}, documents size: {}", 
                recipientIdentifier, documents != null ? documents.size() : 0);
        
        var messages = chatMemory.get(recipientIdentifier);
        log.info("Retrieved {} messages from chat memory for recipientIdentifier: {}", 
                messages != null ? messages.size() : 0, recipientIdentifier);
        
        if (messages.isEmpty()) {
            log.warn("No messages found in chat memory for recipientIdentifier: {}", recipientIdentifier);
            return "ERROR: Please answer the tax advisor questions before generating a summary.";
        }

        String summaryPrompt = String.format(
                "Based on the conversation history, create a professional intake summary narrative.\n\n" +
                "TAX DOCUMENT DATA:\n%s\n" +
                "Total Income: $%.2f\n" +
                "Total Federal Tax: $%.2f\n\n" +
                "Generate a professional narrative summary with 3-4 paragraphs covering:\n\n" +
                "Paragraph 1: Marital status, living situation, and dependent information\n" +
                "Paragraph 2: Financial support details and who can claim dependents\n" +
                "Paragraph 3: Income sources (number of tax documents) and employment/contractor details\n" +
                "Paragraph 4 (if applicable): Any additional relevant tax information discussed\n\n" +
                "Write in third person using past tense. Extract all relevant information from the conversation and format as flowing narrative paragraphs.",
                buildDocumentSummary(documents),
                calculateTotalIncome(documents),
                calculateTotalFederalTax(documents)
        );

        return chatClient.prompt()
                .user(summaryPrompt)
                .advisors(a -> a.param("chat_memory_conversation_id", recipientIdentifier))
                .call()
                .content();
    }

    public void clearHistory(String recipientIdentifier) {
        if (recipientIdentifier != null) {
            chatMemory.clear(recipientIdentifier);
        }
    }

    private String buildDocumentSummary(List<TaxDocumentResult> documents) {
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            TaxDocumentResult doc = documents.get(i);
            String type = doc.getDocument_type();
            summary.append(String.format("\nDocument #%d (%s):\n", i + 1, type));
            summary.append(String.format("  Payer: %s\n", doc.getPayer_name() != null ? doc.getPayer_name() : "N/A"));

            if ("W2".equals(type)) {
                summary.append(String.format("  Wages: $%.2f\n", doc.getWages_box1() != null ? doc.getWages_box1() : 0.0));
                summary.append(String.format("  Federal Tax Withheld: $%.2f\n", doc.getFederal_income_tax_withheld_box2() != null ? doc.getFederal_income_tax_withheld_box2() : 0.0));
            } else if ("1099-NEC".equals(type)) {
                summary.append(String.format("  Nonemployee Compensation: $%.2f\n", doc.getNonemployee_compensation_box1() != null ? doc.getNonemployee_compensation_box1() : 0.0));
                summary.append(String.format("  Federal Tax Withheld: $%.2f\n", doc.getFederal_income_tax_withheld_box4() != null ? doc.getFederal_income_tax_withheld_box4() : 0.0));
            }
        }
        return summary.toString();
    }

    private double calculateTotalIncome(List<TaxDocumentResult> documents) {
        return documents.stream().mapToDouble(doc -> {
            String type = doc.getDocument_type();
            if ("W2".equals(type)) {
                return doc.getWages_box1() != null ? doc.getWages_box1() : 0.0;
            } else if ("1099-NEC".equals(type)) {
                return doc.getNonemployee_compensation_box1() != null ? doc.getNonemployee_compensation_box1() : 0.0;
            }
            return 0.0;
        }).sum();
    }

    private double calculateTotalFederalTax(List<TaxDocumentResult> documents) {
        return documents.stream().mapToDouble(doc -> {
            String type = doc.getDocument_type();
            if ("W2".equals(type)) {
                return doc.getFederal_income_tax_withheld_box2() != null ? doc.getFederal_income_tax_withheld_box2() : 0.0;
            } else if ("1099-NEC".equals(type)) {
                return doc.getFederal_income_tax_withheld_box4() != null ? doc.getFederal_income_tax_withheld_box4() : 0.0;
            }
            return 0.0;
        }).sum();
    }

    private double calculateTotalSSWages(List<TaxDocumentResult> documents) {
        return documents.stream()
                .filter(doc -> "W2".equals(doc.getDocument_type()))
                .mapToDouble(doc -> doc.getSocial_security_wages_box3() != null ? doc.getSocial_security_wages_box3() : 0.0)
                .sum();
    }

    private double calculateTotalMedicareWages(List<TaxDocumentResult> documents) {
        return documents.stream()
                .filter(doc -> "W2".equals(doc.getDocument_type()))
                .mapToDouble(doc -> doc.getMedicare_wages_box5() != null ? doc.getMedicare_wages_box5() : 0.0)
                .sum();
    }

    private double calculateTotalStateIncome(List<TaxDocumentResult> documents) {
        return documents.stream()
                .mapToDouble(doc -> doc.getState_wages() != null ? doc.getState_wages() : 0.0)
                .sum();
    }

    private double calculateTotalStateTax(List<TaxDocumentResult> documents) {
        return documents.stream()
                .mapToDouble(doc -> doc.getState_income_tax() != null ? doc.getState_income_tax() : 0.0)
                .sum();
    }
}
