package edu.school21.userservice.exception;

public class ValidationRequestException extends RuntimeException {

    public ValidationRequestException(String message) {
        super(message);
    }
}
