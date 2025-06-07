package com.vera.server.commands.simpleCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.common.models.View;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PrintFieldDescendingViewCommand extends Command {
    private final CollectionManager collectionManager;
    public PrintFieldDescendingViewCommand(CollectionManager collectionManager) {
        super("print_field_descending_view", "Выводит значения поля view всех элементов в порядке убывания");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

        List<View> sortedList = collectionManager.getCollection().stream()
                .map(Flat::getView)
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .toList();

        StringBuilder sb = new StringBuilder();

        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
        String RESET        = "\u001B[0m";  // Сброс цвета
        sb.append("┌─────────────────────────────────────────────┐\n");
        sb.append(String.format("│ " + HEADER_COLOR + "%-43s" + RESET + " │%n", "View в порядке убывания"));

        int i = 1;
        for (View view : sortedList) {
            sb.append("├─────────────────────────────────────────────┤\n");
            sb.append(String.format("│ %d) %-40s │%n", i++, view.getDescription()));
        }

        sb.append("└─────────────────────────────────────────────┘\n");

        return new CommandResponse(sb.toString());
    }
}
