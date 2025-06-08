package com.vera.client;

import com.vera.client.network.TcpClientManager;
import com.vera.client.utils.Requester;
import com.vera.common.cli.CommandLineReader;

import java.io.IOException;

public final class Client {
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 2613;

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("[Client] Неверный порт, используется " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        CommandLineReader lineReader = new CommandLineReader(System.in, System.out);

        try {
            TcpClientManager tcpClientManager = new TcpClientManager(host, port);
            tcpClientManager.start();

            new Requester(tcpClientManager, lineReader).interactiveMode();
        } catch (IOException e) {
            lineReader.printError("Timeout, не удалось подключиться к серверу");
        }
    }}
