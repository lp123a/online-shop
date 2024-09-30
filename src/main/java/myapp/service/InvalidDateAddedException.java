package myapp.service;

public class InvalidDateAddedException extends RuntimeException {
    public InvalidDateAddedException(String message) {
        super(message);
    }
}
