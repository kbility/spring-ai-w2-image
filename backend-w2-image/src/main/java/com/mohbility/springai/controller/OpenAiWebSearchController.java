package com.mohbility.springai.controller;

import com.mohbility.springai.service.OpenAiWebSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/openai-search")
public class OpenAiWebSearchController {

    private final OpenAiWebSearchService openAiWebSearchService;

    public OpenAiWebSearchController(OpenAiWebSearchService openAiWebSearchService) {
        this.openAiWebSearchService = openAiWebSearchService;
    }

    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody Map<String, String> request) {
        String answer = openAiWebSearchService.queryIRS2025(request.get("question"));
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @GetMapping("/filing-deadlines")
    public ResponseEntity<?> getFilingDeadlines() {
        String answer = openAiWebSearchService.getFilingDeadlines2025();
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @GetMapping("/latest-updates")
    public ResponseEntity<?> getLatestUpdates() {
        String answer = openAiWebSearchService.getLatestIRSUpdates2025();
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @GetMapping("/tax-brackets")
    public ResponseEntity<?> getTaxBrackets() {
        String answer = openAiWebSearchService.getTaxBrackets2025();
        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @GetMapping("/standard-deduction")
    public ResponseEntity<?> getStandardDeduction() {
        String answer = openAiWebSearchService.getStandardDeduction2025();
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
