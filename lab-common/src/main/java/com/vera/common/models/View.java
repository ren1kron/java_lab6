package com.vera.common.models;

import lombok.Getter;

import java.io.Serializable;

/**
 * Перечисление, описывающее вид
 */
public enum View implements Serializable {
    PARK("Парк"),
    NORMAL("Обычный"),
    GOOD("Хороший");

    @Getter
    private final String description;

    View(String description) {
        this.description = description;
    }
}
