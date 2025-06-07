package com.vera.server.commands.simpleCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.commands.Command;

public class ExecuteScriptCommand extends Command {

    public ExecuteScriptCommand() {
        super("execute_script", "Считывает и исполняет скрипт из указанного файла.");
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }
}
