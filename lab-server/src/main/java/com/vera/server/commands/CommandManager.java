package com.vera.server.commands;

import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.elementCommands.AddCommand;
import com.vera.server.commands.simpleCommands.HelpCommand;
import com.vera.server.commands.simpleCommands.InfoCommand;
import com.vera.server.commands.simpleCommands.ShowCommand;
import lombok.Getter;

import java.util.*;

@Getter
public class CommandManager {
    public CommandManager(CollectionManager collectionManager) {
        commandMap.put("help", new HelpCommand(this));
        commandMap.put("info", new InfoCommand(collectionManager));
        commandMap.put("show", new ShowCommand(collectionManager));
        commandMap.put("add", new AddCommand(collectionManager));
//        commandMap.put("update", new UpdateCommand());
//        commandMap.put("remove_by_id", new RemoveByIdCommand());
//        commandMap.put("clear", new ClearCommand());
//        commandMap.put("save", new SaveCommand());
//        commandMap.put("execute_script", new ExecuteScriptCommand());
//        commandMap.put("exit", new ExitCommand());
//        commandMap.put("remove_at", new RemoveAtCommand());
//        commandMap.put("remove_greater", new RemoveGreaterCommand());
//        commandMap.put("history", new HistoryCommand());
//        commandMap.put("sum_of_employees_count", new SumOfEmployeesCountCommand());
//        commandMap.put("filter_starts_with_full_name", new FilterStartsWithFullNameCommand());
//        commandMap.put("print_field_ascending_employees_count", new PrintFieldAscendingEmployeesCountCommand());
    }

    private final Map<String, Command> commandMap = new LinkedHashMap<>();
//    @Getter
//    private static final Deque<Command> history = new LinkedList<>();



    public Command getCommand(String commandName) {
        return commandMap.get(commandName);
    }
//    public void updateHistory(Command command) {
//        history.addLast(command);
//        if (history.size() > 11)
//            history.removeFirst();
//    }

}

