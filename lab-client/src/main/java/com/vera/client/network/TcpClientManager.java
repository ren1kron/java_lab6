package com.vera.client.network;

import com.vera.common.dto.CommandRequest;
import com.vera.common.dto.CommandResponse;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Log
public class TcpClientManager {
    private final String host;
    private final int port;

    // Низкоуровневые ресурсы:
    private SocketChannel channel;
    private Selector selector;
    private ClientAttachment attach;

    public TcpClientManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SneakyThrows
    public void start() {
        log.info(String.format("[Client] Подключение к %s:%d%n", host, port));

        // 1) Открываем SocketChannel
        this.channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));

        // 2) Ждём завершения установки соединения
        while (!channel.finishConnect()) {
            Thread.sleep(10);
        }
        log.info("[Client] Соединение установлено");

        // 3) Открываем селектор и регистрируем канал для OP_READ (ожидание ответа)
        this.selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        this.attach = new ClientAttachment();
    }

    // Отправляем CommandRequest: сериализуем + шлём 4-байтный префикс длины
    public CommandResponse sendRequest(CommandRequest request) throws IOException {
        byte[] body;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            oos.flush();
            body = baos.toByteArray();
        }
        log.info("[Client] Сериализовали CommandRequest: " + request);

        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES + body.length);
        buf.putInt(body.length);
        buf.put(body);
        buf.flip();

        // Пишем всё в канал
        while (buf.hasRemaining()) {
            channel.write(buf);
        }
        log.info("[Client] Отправили запрос на сервер");

        // 3) Читаем ответ (блокируем до получения полного пакета)
        CommandResponse response;
        while (true) {
            selector.select();                            // ждём событие
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isReadable()) {
                    // 3.1) Прочитать длину, если не прочитана
                    if (!attach.isReadingLengthDone()) {
                        int r = channel.read(attach.lenBuffer);
                        if (r == -1) throw new EOFException("Сервер закрыл соединение");
                        if (!attach.isReadingLengthDone()) {
                            continue;
                        }
                        int len = attach.getBodyLength();
                        log.info("[Client] Ожидаем " + len + " байт тела от сервера");
                        attach.prepareBodyBuffer(len);
                    }
                    // 3.2) Прочитать тело
                    int r = channel.read(attach.bodyBuffer);
                    if (r == -1) throw new EOFException("Сервер закрыл соединение");
                    if (!attach.isBodyReadComplete()) {
                        continue;
                    }
                    // 3.3) Десериализовать
                    attach.bodyBuffer.flip();
                    byte[] data = new byte[attach.bodyBuffer.limit()];
                    attach.bodyBuffer.get(data);
                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                        response = (CommandResponse) ois.readObject();
                    } catch (ClassNotFoundException e) {
                        throw new IOException("Ошибка десериализации ответа", e);
                    }
                    log.info("[Client] Получили CommandResponse: " + response);
                    // Готовимся к следующему запросу
                    attach.resetRead();
                    return response;
                }
            }
        }
    }

//    private CommandResponse receiveResponse(SocketChannel channel, Selector selector)
//            throws IOException, ClassNotFoundException {
//        ClientAttachment attach = new ClientAttachment();
//        channel.register(selector, SelectionKey.OP_READ, attach);
//
//        while (true) {
//            selector.select();
//            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
//            while (it.hasNext()) {
//                SelectionKey key = it.next();
//                it.remove();
//
//                if (key.isReadable()) {
//                    SocketChannel sc = (SocketChannel) key.channel();
//
//                    // 1) Читаем длину (4 байта)
//                    if (!attach.isReadingLengthDone()) {
//                        int r = sc.read(attach.lenBuffer);
//                        if (r == -1) throw new EOFException("Сервер закрыл соединение");
//                        if (!attach.isReadingLengthDone()) {
//                            continue;
//                        }
//                        int bodyLength = attach.getBodyLength();
//                        log.info("[Client] Ожидаем " + bodyLength + " байт тела от сервера...");
//                        attach.prepareBodyBuffer(bodyLength);
//                    }
//
//                    // 2) Читаем тело
//                    int r = sc.read(attach.bodyBuffer);
//                    if (r == -1) throw new EOFException("Сервер закрыл соединение");
//                    if (!attach.isBodyReadComplete()) {
//                        continue;
//                    }
//
//                    // 3) Десериализуем CommandResponse
//                    attach.bodyBuffer.flip();
//                    byte[] data = new byte[attach.bodyBuffer.limit()];
//                    attach.bodyBuffer.get(data);
//                    CommandResponse response;
//                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
//                        response = (CommandResponse) ois.readObject();
//                    }
//                    return response;
//                }
//            }
//        }
//    }

    /**
     * Закрыть все ресурсы (канал + селектор).
     */
    public void close() {
        try { if (channel != null) channel.close(); } catch (IOException ignore) {}
        try { if (selector != null) selector.close(); } catch (IOException ignore) {}
        log.info("[Client] Ресурсы закрыты");
    }


    // Состояние для чтения ответа
    private static class ClientAttachment {
        private final ByteBuffer lenBuffer = ByteBuffer.allocate(Integer.BYTES);
        private ByteBuffer bodyBuffer = null;

        boolean isReadingLengthDone() {
            return lenBuffer.position() == Integer.BYTES;
        }

        int getBodyLength() {
            lenBuffer.flip();
            int len = lenBuffer.getInt();
            lenBuffer.clear();
            return len;
        }

        void prepareBodyBuffer(int length) {
            bodyBuffer = ByteBuffer.allocate(length);
        }

        boolean isBodyReadComplete() {
            return bodyBuffer != null && bodyBuffer.remaining() == 0;
        }

        void resetRead() {
            lenBuffer.clear();
            bodyBuffer = null;
        }
    }
}
