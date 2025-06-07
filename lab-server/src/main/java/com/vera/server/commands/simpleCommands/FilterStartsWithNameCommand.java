package com.vera.server.commands.simpleCommands;

import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

import java.util.Vector;
import java.util.stream.Collectors;

public class FilterStartsWithNameCommand extends Command {
    private final CollectionManager collectionManager;
    public FilterStartsWithNameCommand(CollectionManager collectionManager) {
        super("filter_starts_with_full_name fullName", "вывести элементы, значение поля fullName которых начинается с заданной подстроки");
        this.collectionManager = collectionManager;
    }

    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 1) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

        Vector<Flat> filtered = (Vector<Flat>) collectionManager.getCollection()
                .stream()
                .filter(element -> element.getName().startsWith(args[0]))
                .collect(Collectors.toList());

        return new CommandResponse("Отфильтрованная коллекция:", filtered);
//        StringBuilder sb = new StringBuilder();
//        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
//        String RESET        = "\u001B[0m";  // Сброс цвета
//        String RED          = "\u001B[31m"; // Красный цвет
//        String ORANGE       = "\u001B[38;5;214m"; // ANSI 256-цветная палитра, код 214 (оранжевый)
//
//
//        String fullName = args[0];
//
//        sb.append("┌──────────────────────────────────────────────────────────────────┐\n");
//        sb.append(String.format("│ " + HEADER_COLOR + "%-64s" + RESET + " │%n", String.format("Элементы, которые начинаются на '%s'", fullName)));
//
//        for (Flat element : collectionManager) {
//            if (element.getName().startsWith(fullName)) {
//                sb.append("├──────────────────────────────────────────────────────────────────┤\n");
//                sb.append(String.format("│ ID: %-60d │%n", element.getId()));
//                sb.append(String.format("│ Name: %-58s │%n", element.getName()));
//                sb.append(String.format("│ Coordinates: X: %-10d; Y: %-33.2f │%n", element.getCoordinates().getX(), element.getCoordinates().getY()));
//                sb.append(String.format("│ Creation Date: %-49s │%n", element.getCreationDate().toLocalDate()));
//                sb.append(String.format("│ Creation Time: %-49s │%n", element.getCreationDate().toLocalTime()));
//                sb.append(String.format("│ Annual Turnover: %-47d │%n", element.getAnnualTurnover()));
//                sb.append(String.format("│ "+ORANGE+"Full Name: %-53s"+RESET+" │%n", element.getFullName()));
//                sb.append(String.format("│ Employees: %-53d │%n", element.getEmployeesCount()));
//                sb.append(String.format("│ Type: %-58s │%n", element.getType()));
//                sb.append(String.format("│ Postal Address: %-48s │%n", element.getPostalAddress().getZipCode()));
//                sb.append(String.format("│ Location: X: %-5d; Y: %-5d; Name: %-28s │%n",
//                        element.getPostalAddress().getTown().getX(),
//                        element.getPostalAddress().getTown().getY(),
//                        element.getPostalAddress().getTown().getName()));
//            }
//        }
//
//        sb.append("└──────────────────────────────────────────────────────────────────┘\n");
//
//        return new ExecStatus(sb.toString());
    }
}
