package com.vera.client.utils;

import com.vera.client.network.TcpClientManager;
import com.vera.common.cli.BaseCliReader;
import com.vera.common.cli.CommandLineReader;
import com.vera.common.cli.Console;
import com.vera.common.dto.CommandRequest;
import com.vera.common.dto.CommandResponse;
import com.vera.common.exceptions.ExitException;
import com.vera.common.models.Flat;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Requester {
    private final List<String> element_commands = List.of("add", "update");

    private final List<String> scriptStack = new ArrayList<>();
    private final TcpClientManager client;
    private final CommandLineReader console;

    public Requester(TcpClientManager client, CommandLineReader console) {
        this.client = client;
        this.console = console;
    }

    @SneakyThrows
    public void interactiveMode() {
        try {
            String line;
            while ((line = console.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                if (!tokens[0].isEmpty()) {
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


        Flat payload = null;
        if (element_commands.contains(commandName)) {
            payload = console.readFlat();
        }
        return client.sendRequest(new CommandRequest(commandName, argsArray, payload));
    }
}
