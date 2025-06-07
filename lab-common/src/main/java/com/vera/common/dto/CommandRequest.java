package com.vera.common.dto;


import com.vera.common.models.Flat;

import java.io.Serializable;

/**
 * @param commandName "add", "remove_by_id" ...
 * @param args        простые аргументы
 * @param payload     может быть null
 */
public record CommandRequest(String commandName,
                             String[] args,
                             Flat payload) implements Serializable {
}
