package com.vera.server.commands.elementCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class UpdateCommand extends Command {
    private final CollectionManager collectionManager;
    public UpdateCommand(CollectionManager collectionManager) {
        super("update id {element}", "Обновляет элемент коллекции, id которого равен заданному");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat == null || args.length != 1)
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));

        if (collectionManager.update(Long.parseLong(args[0]), flat))
            return new CommandResponse("Организация успешно обновлена!");
        return new CommandResponse(false, "Не удалось обновить коллекцию. Похоже, элемента с таким ID не существует");
    }
}
