package com.vera.common.exceptions;

/**
 * Исключение, возникающее при достижении конца файла(Потока ввода)
 */
public class EofException extends RuntimeException {
    public EofException() {
        super();
    }
}
