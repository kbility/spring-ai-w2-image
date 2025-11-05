package com.mohbility.springai.exception;

public class TaxDocumentException extends RuntimeException {
    public TaxDocumentException(String message) {
        super(message);
    }

    public TaxDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
