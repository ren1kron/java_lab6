package com.vera.server.commands.elementCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class AddCommand extends Command {
    private final CollectionManager collectionManager;
    public AddCommand(CollectionManager collectionManager) {
        super("add {element}", "Добавляет элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat element, String... args) {
        if (element == null || args.length != 0)
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));

        if (collectionManager.add(element))
            return new CommandResponse("Организация успешно добавлена в коллекцию!");
        return new CommandResponse(false, "Не удалось добавить элемент в коллекцию");
    }
}
