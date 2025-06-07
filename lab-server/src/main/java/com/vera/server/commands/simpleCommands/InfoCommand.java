package com.vera.server.commands.simpleCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class InfoCommand extends Command {
    private final CollectionManager collectionManager;
    public InfoCommand(CollectionManager collectionManager) {
        super("info", "Выводит в стандартный поток вывода информацию о коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }


        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
        String RESET        = "\u001B[0m";  // Сброс цвета
        String RED          = "\u001B[31m"; // Красный цвет

        String initTime = (collectionManager.getLastInitTime() == null)
                ? RED + "Коллекция ещё не была инициализирована                      " + RESET
                : "Дата: " + collectionManager.getLastInitTime().toLocalDate() + " | Время: " + collectionManager.getLastInitTime().toLocalTime();

        String saveTime = (collectionManager.getLastSaveTime() == null)
                ? RED + "Коллекция ещё не была сохранена                             " + RESET
                : "Дата: " + collectionManager.getLastSaveTime().toLocalDate() + " | Время: " + collectionManager.getLastSaveTime().toLocalTime();

        StringBuilder sb = new StringBuilder();


        sb.append("┌──────────────────────────────┬──────────────────────────────────────────────────────────────┐\n");
        sb.append(String.format("│ " + HEADER_COLOR + "%-28s" + RESET + " │ " + HEADER_COLOR + "%-61s" + RESET + "│%n", "Атрибут", "Значение"));
        sb.append("├──────────────────────────────┼──────────────────────────────────────────────────────────────┤\n");
        sb.append(String.format("│ %-28s │ %-60s │%n", "Тип коллекции", collectionManager.getCollection().getClass().getSimpleName()));
        sb.append(String.format("│ %-28s │ %-60s │%n", "Время инициализации", initTime));
        sb.append(String.format("│ %-28s │ %-60s │%n", "Время сохранения", saveTime));
        sb.append(String.format("│ %-28s │ %-60d │%n", "Элементов в коллекции", collectionManager.getCollection().size()));
        sb.append("└──────────────────────────────┴──────────────────────────────────────────────────────────────┘\n");

        return new CommandResponse(sb.toString());
    }
}
