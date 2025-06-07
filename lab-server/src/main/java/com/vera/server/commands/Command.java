package com.vera.server.commands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Command {
    @Getter
    private final String name;
    @Getter
    private final String description;

    public abstract CommandResponse execute(Flat flat, String... args);
}
