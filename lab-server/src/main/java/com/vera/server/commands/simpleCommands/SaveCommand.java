package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

import java.io.IOException;

public class SaveCommand extends Command {
    private final CollectionManager collectionManager;
    public SaveCommand(CollectionManager collectionManager) {
        super("save", "Сохраняет коллекцию в файл");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        throw new UnsupportedOperationException("Клиент не может сохранять коллекцию. Это доступно только через консоль сервера");
//        if (flat != null || args.length != 0) {
//            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
//        }
//        try {
//            collectionManager.save();
//            return new CommandResponse("Коллекция успешно сохранена!");
//        } catch (IOException e) {
//            return new CommandResponse(false, "Не удалось сохранить коллекцию в файл");
//        }
    }
}
