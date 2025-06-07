package com.vera.common.exceptions;

/**
 * Исключение, возникающее при некорректном вводе
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }


}
