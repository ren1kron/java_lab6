package com.vera.server.commands.simpleCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.commands.Command;
import com.vera.server.commands.CommandManager;

public class HelpCommand extends Command {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super("help", "Выводит справку по доступным командам");
        this.commandManager = commandManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

        StringBuilder sb = new StringBuilder();

        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
        String RESET        = "\u001B[0m";  // Сброс цвета
                  //                 //
        sb.append("┌──────────────────────────────────────────┬─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐\n");
        sb.append(String.format("│ " + HEADER_COLOR + "%-40s" + RESET + " │ " + HEADER_COLOR + "%-108s" + RESET + "│%n", "Команда", "Описание"));
        sb.append("├──────────────────────────────────────────┼─────────────────────────────────────────────────────────────────────────────────────────────────────────────┤\n");

        for (Command command : commandManager.getCommandMap().values()) {
            sb.append(String.format("│ %-40s │ %-108s│%n", command.getName(), command.getDescription()));
        }

        sb.append("└──────────────────────────────────────────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘\n");



        return new CommandResponse(sb.toString());
    }
}
