package com.vera.client.utils;

import com.vera.client.network.TcpClientManager;
import com.vera.common.cli.BaseCliReader;
import com.vera.common.cli.CommandLineReader;
import com.vera.common.cli.Console;
import com.vera.common.cli.MockPrintStream;
import com.vera.common.dto.CommandRequest;
import com.vera.common.dto.CommandResponse;
import com.vera.common.exceptions.EofException;
import com.vera.common.exceptions.ExitException;
import com.vera.common.models.Flat;
import lombok.SneakyThrows;

import java.io.*;
import java.util.*;

public class Requester {
    private final Set<String> element_commands = Set.of("add", "update", "remove_greater");
    private final int MAX_SCRIPT_DEPTH = 1;

//    private final List<String> scriptStack = new ArrayList<>();
    private final Map<String, Integer> scriptStack = new HashMap<>();
    private final TcpClientManager client;
    private BaseCliReader console;

    public Requester(TcpClientManager client, BaseCliReader console) {
        this.client = client;
        this.console = console;
    }

    @SneakyThrows
    public void interactiveMode() {
        try {
            String line;
            while ((line = console.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (tokens.length == 0 || tokens[0].isEmpty())
                    continue;
                if (tokens[0].equals("exit"))
                    break;

                CommandResponse response = runCommand(tokens);
                if (response.isOk()) {
                    console.println(response.message());
                    if (response.data() != null) {
                        console.println(response.data());
                    }
                } else {
                    console.printError(response.message());
                }
            }
        } catch (ExitException e) {
            console.printError("Закрываем программу...");
        } finally {
            client.close();
        }
    }

    private CommandResponse runCommand(String[] tokens) throws ExitException, IOException {
        String commandName = tokens[0];
        List<String> argsList = new ArrayList<>();
        if (tokens.length > 1) {
            argsList.addAll(Arrays.asList(tokens).subList(1, tokens.length));
        }
        String[] argsArray = argsList.toArray(new String[0]);

        if (commandName.equals("execute_script")) {
            if (argsArray.length != 1)
                return new CommandResponse(false, "Команде 'execute_script' были переданы невалидные аргументы\nВведите 'help' для справки.");
            console.println(String.format("Запускаем скрипт '%s'", tokens[1]));
            return scriptMode(argsArray[0]);
        }

        Flat payload = null;
        if (element_commands.contains(commandName)) {
            payload = console.readFlat();
        }
        return client.sendRequest(new CommandRequest(commandName, argsArray, payload));
    }

    private CommandResponse scriptMode(String filename) {
        // ограничиваем глубину рекурсии
        scriptStack.merge(filename, 1, Integer::sum);
        if (scriptStack.get(filename) > MAX_SCRIPT_DEPTH) {
            scriptStack.merge(filename, -1, Integer::sum);
            return new CommandResponse(false,
                    "Превышен максимально допустимый уровень вложенности скриптов ("
                            + MAX_SCRIPT_DEPTH + ")", null);
        }

        var oldConsole = console;
        try {

            console = new BaseCliReader(new FileInputStream(filename), new MockPrintStream());
            while (true) {
                String line;
                try {
                    line = console.readLine();
                } catch (EofException eof) {
                    break;
                }

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0 || tokens[0].isEmpty()) continue;

                // Защита от бесконечной рекурсии
                if ("execute_script".equals(tokens[0])
                        && scriptStack.getOrDefault(tokens[1], 0) >= MAX_SCRIPT_DEPTH) {
                    console.printError("Достигнут предел рекурсии при execute_script '"
                            + tokens[1] + "'");
                    continue;
                }

                CommandResponse resp;
                try {
                    resp = runCommand(tokens);
                } catch (ExitException ee) {
                    console.printError("Скрипт принудительно завершён командой exit");
                    return new CommandResponse(false, "Скрипт прерван.", null);
                } catch (IOException ioe) {
                    console.printError("Ошибка сети при выполнении скрипта: " + ioe.getMessage());
                    return new CommandResponse(false, "Прервано из-за сетевой ошибки.", null);
                }

                if (resp.isOk()) {
                    console.println(resp.message());
                    if (resp.data() != null) {
                        console.println(resp.data());
                    }
                } else {
                    console.printError(resp.message());
                }
            }
            return new CommandResponse(true, "Скрипт выполнен успешно.", null);
        } catch (FileNotFoundException fnf) {
            return new CommandResponse(false, "Файл не найден: " + filename, null);
        } finally {
            console = oldConsole;
            scriptStack.merge(filename, -1, Integer::sum);
            if (scriptStack.get(filename) <= 0) {
                scriptStack.remove(filename);
            }
        }
    }
}
