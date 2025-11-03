package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaxDocumentCacheService {

    private final Map<String, List<TaxDocumentResult>> cache = new HashMap<>();

    public void cache(TaxDocumentResult result) {
        if (result != null && result.getRecipient_name() != null) {
            cache.computeIfAbsent(result.getRecipient_name(), k -> new ArrayList<>()).add(result);
        }
    }

    public List<TaxDocumentResult> get(String recipientName) {
        return cache.get(recipientName);
    }

    public void clear() {
        cache.clear();
    }
}
