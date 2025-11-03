package com.mohbility.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class GeneralChatService {

    private static final String CONVERSATION_ID = "general-tax-chat";
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public GeneralChatService(ChatClient.Builder builder) {
        this.chatMemory = new InMemoryChatMemory();
        this.chatClient = builder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }

    public String chat(String message) {
        String systemPrompt = 
            "You are a professional tax advisor. A client is asking you questions without providing W-2 forms yet.\n\n" +
            "Your role is to:\n" +
            "- NEVER repeat questions you've already asked\n" +
            "- Use the client's name when they provide it to personalize the conversation\n" +
            "- Ask probing questions to understand their tax situation (one at a time)\n" +
            "- Gather information about income, marital status, dependents, and household support\n" +
            "- Provide helpful tax guidance based on the information they share\n" +
            "- Be conversational, warm, and friendly\n\n" +
            "CONVERSATION FLOW (ask only if not already answered):\n" +
            "1. If they just provided their name, thank them and ask about their annual income\n" +
            "2. Then ask about marital status (single, married, divorced, widowed)\n" +
            "3. Then ask about number of dependents/children\n" +
            "4. Then ask who provides financial support for household\n" +
            "5. Provide tax guidance based on information gathered\n\n" +
            "Respond naturally and helpfully (do NOT repeat questions already asked).";

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, CONVERSATION_ID))
                .call()
                .content();
    }

    public String generateSummary() {
        var messages = chatMemory.get(CONVERSATION_ID, 100);
        if (messages.isEmpty()) {
            return "ERROR: Please answer some questions before generating a summary.";
        }

        String summaryPrompt = 
            "Based on the conversation history, create a professional intake summary narrative.\n\n" +
            "Generate a professional narrative summary with 3-4 paragraphs covering:\n\n" +
            "Paragraph 1: Client's name, marital status, living situation, and dependent information\n" +
            "Paragraph 2: Financial support details and who can claim dependents\n" +
            "Paragraph 3: Income sources and employment details\n" +
            "Paragraph 4 (if applicable): Any additional relevant tax information discussed\n\n" +
            "Example format:\n" +
            "The taxpayer reported that she is not married and lived with her dependent child for the entire previous year. She stated that no one else can claim her child as a dependent because she is the sole provider for the household. She provided one W-2 form as her only source of income.\n\n" +
            "Write in third person using past tense (\"reported\", \"stated\", \"provided\"). Extract all relevant information from the conversation and format as flowing narrative paragraphs.";

        return chatClient.prompt()
                .user(summaryPrompt)
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, CONVERSATION_ID))
                .call()
                .content();
    }

    public void clearHistory() {
        chatMemory.clear(CONVERSATION_ID);
    }
}
