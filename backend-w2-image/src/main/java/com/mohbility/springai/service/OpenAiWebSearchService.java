package com.mohbility.springai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OpenAiWebSearchService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiWebSearchService.class);
    private static final String MODEL = "gpt-4o-search-preview";
    private static final String TAX_REJECTION_MESSAGE = "I can only answer questions related to US federal taxes and IRS matters. Please ask a tax-related question.";
    private static final String SYSTEM_PROMPT = "You are an IRS information specialist. Search IRS.gov for official 2025 tax year information. " +
            "Provide accurate answers with specific IRS publication references. " +
            "Format: **Answer:** [answer] **IRS Source:** [source] **Last Verified:** [date]";
    
    private final ChatClient chatClient;
    private final ChatClient validationClient;

    public OpenAiWebSearchService(@Value("${spring.ai.openai.api-key}") String apiKey) {
        OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
        
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model(MODEL)
                .build();

        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .build();

        this.chatClient = ChatClient.builder(openAiChatModel).build();
        
        // Separate client for validation without web search
        OpenAiChatModel validationModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model("gpt-4o-mini").build())
                .build();
        this.validationClient = ChatClient.builder(validationModel).build();
    }

    public String queryIRS2025(String question) {
        return queryIRS2025(question, true);
    }

    private String queryIRS2025(String question, boolean validateTaxRelated) {
        try {
            log.info("Querying IRS 2025 information: {}", question);

            if (validateTaxRelated && !isTaxRelated(question)) {
                return TAX_REJECTION_MESSAGE;
            }

            OpenAiApi.ChatCompletionRequest.WebSearchOptions webSearchOptions = 
                new OpenAiApi.ChatCompletionRequest.WebSearchOptions(
                    OpenAiApi.ChatCompletionRequest.WebSearchOptions.SearchContextSize.LOW,
                    null
                );

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .webSearchOptions(webSearchOptions)
                    .build();

            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(question + " (2025 tax year)")
                    .options(options)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("Error querying OpenAI web search", e);
            return "Error retrieving IRS information. Please try again later.";
        }
    }

    private boolean isTaxRelated(String question) {
        try {
            String validationPrompt = "Is this question related to US federal taxes, IRS, AGI (Adjusted Gross Income), " +
                    "tax deductions, tax credits, tax filing, tax brackets, standard deduction, or income tax calculations? " +
                    "Answer ONLY 'YES' or 'NO': " + question;
            
            String validation = validationClient.prompt()
                    .user(validationPrompt)
                    .call()
                    .content();
            
            boolean result = validation != null && validation.trim().toUpperCase().startsWith("YES");
            log.info("Tax validation for '{}': {} (response: {})", question, result, validation);
            return result;
        } catch (Exception e) {
            log.error("Validation error, rejecting question", e);
            return false;
        }
    }

    public String getFilingDeadlines2025() {
        return queryIRS2025("What are the tax filing deadlines and important dates", false);
    }

    public String getLatestIRSUpdates2025() {
        return queryIRS2025("What are the latest IRS updates and announcements for 2025 tax year?", false);
    }

    public String getTaxBrackets2025() {
        return queryIRS2025("What are the federal income tax brackets for 2025?", false);
    }

    public String getStandardDeduction2025() {
        return queryIRS2025("What is the standard deduction for 2025?", false);
    }
}
