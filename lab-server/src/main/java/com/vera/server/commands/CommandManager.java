package com.vera.server.commands;

import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.elementCommands.AddCommand;
import com.vera.server.commands.elementCommands.RemoveGreaterCommand;
import com.vera.server.commands.elementCommands.UpdateCommand;
import com.vera.server.commands.simpleCommands.*;
import lombok.Getter;

import java.util.*;

@Getter
public class CommandManager {
    public CommandManager(CollectionManager collectionManager) {
        commandMap.put("help", new HelpCommand(this));
        commandMap.put("info", new InfoCommand(collectionManager));
        commandMap.put("show", new ShowCommand(collectionManager));
        commandMap.put("add", new AddCommand(collectionManager));
        commandMap.put("update", new UpdateCommand(collectionManager));
        commandMap.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commandMap.put("clear", new ClearCommand(collectionManager));
        commandMap.put("save", new SaveCommand(collectionManager));
        commandMap.put("execute_script", new ExecuteScriptCommand());
        commandMap.put("exit", new ExitCommand());
        commandMap.put("remove_first", new RemoveFirstCommand(collectionManager));
        commandMap.put("remove_greater", new RemoveGreaterCommand(collectionManager));
        commandMap.put("filter_starts_with_name", new FilterStartsWithNameCommand(collectionManager));
        commandMap.put("print_unique_number_of_rooms", new PrintUniqueNumberOfRoomsCommand(collectionManager));
        commandMap.put("print_field_descending_view", new PrintFieldDescendingViewCommand(collectionManager));
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

