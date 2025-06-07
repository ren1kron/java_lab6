package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.common.models.View;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueNumberOfRoomsCommand extends Command {
    private final CollectionManager collectionManager;
    public PrintUniqueNumberOfRoomsCommand(CollectionManager collectionManager) {
        super("print_unique_number_of_rooms", "Выводит уникальные значения поля numberOfRooms всех элементов в коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

        Set<Long> uniques = collectionManager.getCollection().stream()
                .map(Flat::getNumberOfRooms)
                .collect(Collectors.toSet());

        StringBuilder sb = new StringBuilder();

        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
        String RESET        = "\u001B[0m";  // Сброс цвета
        sb.append("┌─────────────────────────────────────────────┐\n");
        sb.append(String.format("│ " + HEADER_COLOR + "%-43s" + RESET + " │%n", "View в порядке убывания"));

        int i = 1;
        for (long l : uniques) {
            sb.append("├─────────────────────────────────────────────┤\n");
            sb.append(String.format("│ %d) %-40d │%n", i++, l));
        }

        sb.append("└─────────────────────────────────────────────┘\n");


        return new CommandResponse(sb.toString());
    }
}
