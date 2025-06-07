package com.vera.server.commands.simpleCommands;


import com.vera.common.dto.CommandResponse;
import com.vera.common.models.Flat;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;

public class ShowCommand extends Command {
    private final CollectionManager collectionManager;
    public ShowCommand(CollectionManager collectionManager) {
        super("show", "Выводит в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }
    @Override
    public CommandResponse execute(Flat flat, String... args) {
        if (flat != null || args.length != 0) {
            throw new IllegalArgumentException(String.format("Команде '%s' были переданы невалидные аргументы. Введите 'help' для справки.", getName()));
        }

//        StringBuilder sb = new StringBuilder();
//        String HEADER_COLOR = "\u001B[34m"; // Синий цвет заголовка
//        String RESET = "\u001B[0m";  // Сброс цвета
//
//        sb.append("┌──────────────────────────────────────────────────────────────────┐\n");
//        sb.append(String.format("│ " + HEADER_COLOR + "%-64s" + RESET + " │%n", "Collection Contents"));
//
//        for (Flat element : collectionManager.getCollection()) {
//            sb.append("├──────────────────────────────────────────────────────────────────┤\n");
//            sb.append(String.format("│ ID: %-60d │%n", element.getId()));
//            sb.append(String.format("│ Name: %-58s │%n", element.getName()));
//            sb.append(String.format("│ Coordinates: X: %-10d; Y: %-33.2f │%n", element.getCoordinates().getX(), element.getCoordinates().getY()));
//            sb.append(String.format("│ Creation Date: %-49s │%n", element.getCreationDate().toLocalDate()));
//            sb.append(String.format("│ Creation Time: %-49s │%n", element.getCreationDate().toLocalTime()));
//            sb.append(String.format("│ Annual Turnover: %-47d │%n", element.getAnnualTurnover()));
//            sb.append(String.format("│ Full Name: %-53s │%n", element.getFullName()));
//            sb.append(String.format("│ Employees: %-53d │%n", element.getEmployeesCount()));
//            sb.append(String.format("│ Type: %-58s │%n", element.getType()));
//            sb.append(String.format("│ Postal Address: %-48s │%n", element.getPostalAddress().getZipCode()));
//            sb.append(String.format("│ Location: X: %-5d; Y: %-5d; Name: %-28s │%n",
//                    element.getPostalAddress().getTown().getX(),
//                    element.getPostalAddress().getTown().getY(),
//                    element.getPostalAddress().getTown().getName()));
//        }
//
//        sb.append("└──────────────────────────────────────────────────────────────────┘\n");

        return new CommandResponse("Элементы коллекции:", collectionManager.getCollection());
    }

}
