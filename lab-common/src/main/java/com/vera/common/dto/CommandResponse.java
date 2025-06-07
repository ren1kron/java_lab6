package com.vera.common.dto;

import com.vera.common.models.Flat;

import java.io.Serializable;
import java.util.Vector;

/**
 * @param message human-readable
 * @param data    ответ (может быть пустым)
 */
public record CommandResponse(boolean isOk, String message, Vector<Flat> data) implements Serializable {
    public CommandResponse(String message) {
        this(true, message, null);
    }
    public CommandResponse(String message, Vector<Flat> data) {
        this(true, message, data);
    }

    public CommandResponse(boolean isOk, String message) {
        this(isOk, message, null);
    }

    @Override
    public String toString() {
        return message;
    }
}
