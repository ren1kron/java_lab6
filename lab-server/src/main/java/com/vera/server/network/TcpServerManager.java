package com.vera.server.network;

import com.vera.common.cli.CommandLineReader;
import com.vera.common.dto.CommandRequest;
import com.vera.common.dto.CommandResponse;
import com.vera.server.collectionManagement.CollectionManager;
import com.vera.server.commands.Command;
import com.vera.server.commands.CommandManager;
import lombok.extern.java.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

@Log
public class TcpServerManager {
    private InetSocketAddress address;
    private Selector selector;

    private final CommandManager commandManager;
    private final CollectionManager collectionManager;

    public TcpServerManager(InetSocketAddress address, CommandManager commandManager, CollectionManager collectionManager) {
        this.address = address;
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    public void start() throws IOException {
        log.info(String.format("[Server] Запуск на порту %d%n", address.getPort()));

        this.selector = Selector.open();

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.bind(this.address);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        log.info("[Server] ожидаем подключений...");

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // Мы не можем использовать многопоточку, поэтому мы в одном цикле считываем команды, которые поступают
            // в консоль сервера и обрабатываем запросы от клиентов

            // пытаемся прочитать команду из консоли
            if (console.ready()) {
                String input = console.readLine().trim();

                if (input.equals("save")) {
                    collectionManager.save();
                    log.info("Коллекция успешно сохранена");
                } else if (input.equals("exit")) {
                    log.info("Завершаем работу сервера...");
                    socketChannel.close();
                    selector.close();
                    System.exit(1);
                } else if (input.isEmpty()) {
                    continue;
                } else {
                    log.warning("Введённая команда не поддерживается сервером. На сервере можно выполнить команды: 'exit', 'save'");
                }
            }

            // принимаем запрос клиента
            selector.select(1);

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                try {
                    if (!key.isValid())
                        continue;
                    if (key.isAcceptable())
                        handleAccept(key);
                    else if (key.isReadable())
                        handleRead(key);
                    else if (key.isWritable())
                        handleWrite(key);
                } catch (IOException ex) {
                    log.severe("[Server] Ошибка на канале: " + ex.getMessage());
                    closeKey(key);
                }
            }
        }
    }


    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);

        ClientAttachment attach = new ClientAttachment();
        client.register(selector, SelectionKey.OP_READ, attach);


        log.info("[Server] Принято новое соединение от " + client.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientAttachment attach = (ClientAttachment) key.attachment();

        // 1) Читаем префикс длины, если ещё не прочитали полностью
        if (!attach.isReadingLengthDone()) {
            int r = client.read(attach.lenBuffer);
            if (r == -1) {
                closeKey(key);
                return;
            }
            if (!attach.isReadingLengthDone()) {
                // ещё не накапали 4 байта
                return;
            }
            // получили 4 байта → определяем размер тела
            int bodyLength = attach.getBodyLength();
            System.out.println("[Server] Ожидаем " + bodyLength + " байт тела от клиента");
            attach.prepareBodyBuffer(bodyLength);
        }

        // 2) Читаем тело, если выделили bodyBuffer
        int r = client.read(attach.bodyBuffer);
        if (r == -1) {
            closeKey(key);
            return;
        }
        if (!attach.isBodyReadComplete()) {
            // ещё не дочитали всё тело
            return;
        }

        // 3) Теперь тело полностью прочитано -> десериализуем CommandRequest
        attach.bodyBuffer.flip();
        byte[] data = new byte[attach.bodyBuffer.limit()];
        attach.bodyBuffer.get(data);
        attach.clearBodyBuffer();

        CommandRequest request = deserializeRequest(data);
        System.out.println("[Server] Получен CommandRequest: " + request);

        // 4) Формируем CommandResponse
        CommandResponse response;

        Command command = commandManager.getCommand(request.commandName());

        if (commandManager.getCommand(request.commandName()) == null) {
            response = new CommandResponse(false, "Такой команды не существует");
        } else {
            try {
                response = command.execute(request.payload(), request.args());
            } catch (UnsupportedOperationException e) {
                response = new CommandResponse(false, e.getMessage());
            }
        }

        // 5) Сериализуем ответ и кладём в очередь на запись
        byte[] respBytes = serializeResponse(response);
        ByteBuffer outBuf = ByteBuffer.allocate(Integer.BYTES + respBytes.length);
        outBuf.putInt(respBytes.length);
        outBuf.put(respBytes);
        outBuf.flip();

        attach.enqueueResponse(outBuf);
        // переключаем interest на OP_WRITE, чтобы отправить ответ
        key.interestOps(SelectionKey.OP_WRITE);

        attach.resetRead();
    }

    // Обработка записи ответа клиенту
    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientAttachment attach = (ClientAttachment) key.attachment();

        Queue<ByteBuffer> queue = attach.getWriteQueue();
        while (!queue.isEmpty()) {
            ByteBuffer buf = queue.peek();
            client.write(buf);
            if (buf.remaining() > 0) {
                // не успели записать весь буфер, ждем следующего OP_WRITE
                return;
            }
            queue.poll();
        }
        // как только очередь пуста, возвращаем interest обратно на OP_READ
        key.interestOps(SelectionKey.OP_READ);
        System.out.println("[Server] Ответ отправлен, возвращаемся в ожидание запросов");
    }

    // «Закрываем» канал и отменяем ключ
    private void closeKey(SelectionKey key) {
        try {
            key.channel().close();
        } catch (IOException ignored) { }
        key.cancel();
    }

    // Десериализация CommandRequest из байтов
    private CommandRequest deserializeRequest(byte[] raw) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(raw))) {
            return (CommandRequest) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Не удалось десериализовать CommandRequest", e);
        }
    }

    // Сериализация CommandResponse в байты
    private byte[] serializeResponse(CommandResponse resp) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(resp);
            oos.flush();
            return baos.toByteArray();
        }
    }

    // Состояние каждого клиентского соединения: буферы для чтения длины и тела, очередь ответов
    private static class ClientAttachment {
        // читаем сначала 4-байтный префикс длины
        private final ByteBuffer lenBuffer = ByteBuffer.allocate(Integer.BYTES);
        // когда знаем длину, выделим bodyBuffer
        private ByteBuffer bodyBuffer = null;
        // очередь буферов, которые нужно записать клиенту
        private final Queue<ByteBuffer> writeQueue = new LinkedList<>();

        // флаг, что длину (4 байта) уже полностью прочитали
        boolean isReadingLengthDone() {
            return lenBuffer.position() == Integer.BYTES;
        }
        // возвращает заранее прочитанное length-значение
        int getBodyLength() {
            lenBuffer.flip();
            int length = lenBuffer.getInt();
            lenBuffer.clear();
            return length;
        }
        // готовим буфер для чтения тела заданной длины
        void prepareBodyBuffer(int length) {
            bodyBuffer = ByteBuffer.allocate(length);
        }
        // сброс чтения тела (после того, как тело обработано)
        void clearBodyBuffer() {
            bodyBuffer = null;
        }
        // Проверка, что тело уже полностью прочитано
        boolean isBodyReadComplete() {
            return bodyBuffer != null && bodyBuffer.remaining() == 0;
        }
        // сбрасываем буферы для чтения следующего сообщения
        void resetRead() {
            lenBuffer.clear();
            bodyBuffer = null;
        }
        // добавляем ByteBuffer-ответ в очередь
        void enqueueResponse(ByteBuffer buf) {
            writeQueue.add(buf);
        }
        // получаем очередь для записи
        Queue<ByteBuffer> getWriteQueue() {
            return writeQueue;
        }
    }
}
