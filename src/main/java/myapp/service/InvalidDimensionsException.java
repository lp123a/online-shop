package myapp.service;

public class InvalidDimensionsException extends RuntimeException {
    public InvalidDimensionsException(String message) {
        super(message);
    }
}
