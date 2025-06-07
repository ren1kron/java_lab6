package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class ClearCommand extends Command {
    private final CollectionManager collectionManager;
    public ClearCommand(CollectionManager collectionManager) {
        super("clear", "Очищает коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

        collectionManager.clear();
        return new CommandResponse("Коллекция была успешно очищена");
    }
}
