package com.vera.common.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Заглушка для {@link PrintStream}
 * Данный поток не выполняет никаких записей
 * @see PrintStream
 */
public class MockPrintStream extends PrintStream {


    public MockPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
    }
}
