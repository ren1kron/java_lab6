package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class RemoveFirstCommand extends Command {
    private final CollectionManager collectionManager;
    public RemoveFirstCommand(CollectionManager collectionManager) {
        super("remove_at index", "Удаляет элемент из коллекции по его индексу (позиции в коллекции)");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0)
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));

        try {
            collectionManager.removeByIndex(0);
            return new CommandResponse("Элемент был успешно удалён");
        } catch (IndexOutOfBoundsException e) {
            return new CommandResponse(false, "Индекс вышел за пределы коллекции");
        }
    }
}
