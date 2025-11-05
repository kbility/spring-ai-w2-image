package com.mohbility.springai.service;

import com.mohbility.springai.model.TaxDocumentResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaxDocumentCacheService {

    private final Map<String, List<TaxDocumentResult>> cache = new ConcurrentHashMap<>();

    public void cache(TaxDocumentResult result) {
        if (result == null || result.getRecipient_name() == null) {
            return;
        }
        cache.computeIfAbsent(result.getRecipient_name(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(result);
    }

    public List<TaxDocumentResult> get(String recipientName) {
        return cache.getOrDefault(recipientName, Collections.emptyList());
    }

    public boolean exists(String recipientName) {
        return cache.containsKey(recipientName);
    }

    public void clear() {
        cache.clear();
    }

    public void clearByRecipient(String recipientName) {
        cache.remove(recipientName);
    }
    
    public String getFirstRecipientName() {
        return cache.keySet().stream().findFirst().orElse(null);
    }
}
