package com.mohbility.springai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class OpenAiWebSearchService {

    private static final Logger log = LoggerFactory.getLogger(OpenAiWebSearchService.class);
    private static final String MODEL = "gpt-4o-search-preview";
    private static final String VALIDATION_MODEL = "gpt-4o-mini";
    private static final String TAX_REJECTION_MESSAGE = "I can only answer questions related to U.S. federal taxes and IRS matters. Please ask a tax-related question.";
    
    private final ChatClient chatClient;
    private final ChatClient validationClient;
    private final String systemPrompt;

    public OpenAiWebSearchService(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("classpath:prompts/tax-assist-pro-system-prompt.txt") org.springframework.core.io.Resource promptResource
    ) throws java.io.IOException {
        this.systemPrompt = promptResource.getContentAsString(StandardCharsets.UTF_8);
        OpenAiApi openAiApi = OpenAiApi.builder().apiKey(apiKey).build();
        
        OpenAiChatModel searchModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model(MODEL).build())
                .build();
        this.chatClient = ChatClient.builder(searchModel).build();
        
        OpenAiChatModel validationModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model(VALIDATION_MODEL).build())
                .build();
        this.validationClient = ChatClient.builder(validationModel).build();
    }

    public String queryIRS2025(String question) {
        return queryIRS2025(question, true);
    }

    private String queryIRS2025(String question, boolean validateTaxRelated) {
        try {
            log.info("Querying IRS 2025: {}", question);

            if (validateTaxRelated && !isTaxRelated(question)) {
                log.warn("Rejected non-tax question: {}", question);
                return TAX_REJECTION_MESSAGE;
            }

            String userPrompt = buildUserPrompt(question);

            OpenAiApi.ChatCompletionRequest.WebSearchOptions webSearchOptions = 
                new OpenAiApi.ChatCompletionRequest.WebSearchOptions(
                    OpenAiApi.ChatCompletionRequest.WebSearchOptions.SearchContextSize.MEDIUM,
                    null
                );

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .webSearchOptions(webSearchOptions)
                    .build();

            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .options(options)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("Error during IRS query", e);
            return "Error retrieving IRS information. Please try again later.";
        }
    }

    private boolean isTaxRelated(String question) {
        try {
            String validationPrompt = """
                Determine if the following question relates to U.S. federal income taxes or IRS topics such as:
                - tax brackets, deductions, credits
                - filing status, dependents
                - tax forms (1040, W-2, 1099, etc.)
                - IRS publications or policies
                - AGI, taxable income calculations
                
                Respond only with "YES" or "NO".
                
                Question: %s
                """.formatted(question);
            
            String validation = validationClient.prompt()
                    .user(validationPrompt)
                    .call()
                    .content();
            
            boolean result = validation != null && validation.trim().toUpperCase().startsWith("YES");
            log.info("Validation for '{}': {} (model said '{}')", question, result, validation);
            return result;
        } catch (Exception e) {
            log.error("Validation model error", e);
            return false;
        }
    }

    private String buildUserPrompt(String question) {
        return """
            CRITICAL: This question is about TAX YEAR 2025 (filing in 2026).
            This is for income earned in 2025, to be filed in 2026.
            Do NOT provide information for tax year 2024 or any other year.
            
            USER CONTEXT:
            Tax Year: 2025 (filing in 2026)
            Income Year: 2025
            Filing Year: 2026
            
            USER QUESTION:
            %s
            
            ---
            INSTRUCTION TO ASSISTANT:
            1. Search ONLY for tax year 2025 information from IRS.gov.
            2. If the user provides specific income amounts and asks for calculations (EITC, tax owed, etc.):
               - Use the official 2025 IRS tables, formulas, and thresholds from IRS.gov
               - Perform the calculation step-by-step showing the formula used
               - Cite the specific IRS publication or table used (e.g., "IRS Publication 596, EITC Table for 2025")
               - Clearly state any assumptions made
            3. If 2025 information is not yet available, explicitly state: "Tax year 2025 information is not yet published by the IRS. The most recent available is [year]."
            4. NEVER provide 2024 or prior year information without clearly stating the year.
            5. Provide a factual, compliant, and concise answer with IRS.gov sources.
            6. End with: "This is an educational estimate based on IRS guidelines. For your actual tax situation, please consult a tax professional or use official IRS tools."
            """.formatted(question);
    }

    public String getFilingDeadlines2025() {
        return queryIRS2025("What are the tax year 2025 IRS tax filing deadlines (filing in 2026)?", false);
    }

    public String getLatestIRSUpdates2025() {
        return queryIRS2025("What are the latest IRS updates and announcements for tax year 2025?", false);
    }

    public String getTaxBrackets2025() {
        return queryIRS2025("What are the tax year 2025 federal income tax brackets?", false);
    }

    public String getStandardDeduction2025() {
        return queryIRS2025("What is the standard deduction amount for tax year 2025?", false);
    }
}
