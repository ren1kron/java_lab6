package com.vera.server.commands.elementCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class RemoveGreaterCommand extends Command {
    private final CollectionManager collectionManager;
    public RemoveGreaterCommand(CollectionManager collectionManager) {
        super("remove_greater {element}", "Удаляет из коллекции все Организации, чей годовой оборот больше, чем у заданной");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat == null || args.length != 1)
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));

        collectionManager.removeGreater(flat);
        return new CommandResponse("Все элементы с годовым оборотом выше заданного были удалены");
    }
}
