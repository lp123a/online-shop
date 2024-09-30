package myapp.service;

public class InvalidKeywordsException extends RuntimeException {
    public InvalidKeywordsException(String message) {
        super(message);
    }
}
