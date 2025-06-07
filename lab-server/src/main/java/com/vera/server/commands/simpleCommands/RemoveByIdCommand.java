package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class RemoveByIdCommand extends Command {
    private final CollectionManager collectionManager;
    public RemoveByIdCommand(CollectionManager collectionManager) {
        super("remove_by_id id", "Удаляет элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 1)
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));

        if (collectionManager.removeById(Long.parseLong(args[0])))
            return new CommandResponse("Элемент был успешно удалён");
        return new CommandResponse(false, "Не удалось удалить элемент");
    }
}
