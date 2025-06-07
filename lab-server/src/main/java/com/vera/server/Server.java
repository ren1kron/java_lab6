package com.vera.server;

import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.collectionManagement.DumpManager;
import com.vera.server.commands.CommandManager;
import com.vera.server.network.TcpServerManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class Server {
    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    private static final int DEFAULT_PORT = 2613;
    private static final String DEFAULT_COLLECTION_PATH = "collection.json";

    public static void main(String[] args) throws IOException {
        CollectionManager collectionManager = new CollectionManager(DEFAULT_COLLECTION_PATH);
        CommandManager commandManager = new CommandManager(collectionManager);

        InetSocketAddress address = new InetSocketAddress(DEFAULT_PORT);
        new TcpServerManager(address, commandManager, collectionManager).start();


    }
}
