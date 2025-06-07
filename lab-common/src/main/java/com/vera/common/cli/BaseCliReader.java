package com.vera.common.cli;

import com.vera.common.exceptions.EofException;
import com.vera.common.models.*;

import java.io.*;

/**
 * Базовый класс для чтения данных из консоли
 */
public class BaseCliReader {

    private static final String P1 = "$ ";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    protected final BufferedReader in;

    protected final PrintStream out;

    public BaseCliReader(InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }


    /**
     * Читает строку из консоли
     *
     * @return строка
     */
    public String readLine() {
        try {
            String line = in.readLine();
            if (line == null) {
                throw new EofException();
            }
            return line.strip().trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * Читает {@link Flat#name} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readName(Flat.Builder builder) {
        final String inputMsg = "Введите имя: ";
        out.print(inputMsg);

        String input = readLine();
        builder.name(input);
    }
    /**
     * Читает {@link Flat#coordinates} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readCoordinates(Flat.Builder builder) {
        final String inputMsg = "Введите координаты(x y): ";
        out.print(inputMsg);

        String input = readLine();

        String[] coords = input.strip().trim().split(" +");

        builder.coordinates(new Coordinates(Integer.valueOf(coords[0]), Float.parseFloat(coords[1])));
    }

    /**
     * Читает {@link Flat#area} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readArea(Flat.Builder builder) {
        final String inputMsg = "Введите площадь: ";
        out.print(inputMsg);

        String input = readLine();
        builder.area(Float.parseFloat(input));
    }

    /**
     * Читает {@link Flat#numberOfRooms} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readNumberOfRooms(Flat.Builder builder) {
        final String inputMsg = "Введите кол-во комнат: ";
        out.print(inputMsg);

        String input = readLine();
        builder.numberOfRooms(Long.parseLong(input));
    }

    /**
     * Читает {@link Flat#furnish} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readFurnish(Flat.Builder builder) {
        final String inputMsg = "Введите Furnish(DESIGNER, NONE, FINE, BAD, LITTLE): ";
        out.print(inputMsg);

        String input = readLine();
        builder.furnish(Furnish.valueOf(input.toUpperCase().trim()));
    }

    /**
     * Читает {@link Flat#view} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readView(Flat.Builder builder) {
        final String inputMsg = "Введите View(PARK, NORMAL, GOOD): ";
        out.print(inputMsg);
        String input = readLine();
        builder.view(View.valueOf(input.toUpperCase().trim()));
    }

    /**
     * Читает {@link Flat#transport} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readTransport(Flat.Builder builder) {
        final String inputMsg = "Введите Transport(FEW, NORMAL, ENOUGH): ";
        out.print(inputMsg);

        String input = readLine();
        builder.transport(Transport.valueOf(input.toUpperCase().trim()));
    }

    /**
     * Читает {@link House#name} из консоли
     * @return название дома
     */
    protected String readHouseName() {
        final String inputMsg = "название дома: ";
        out.print(inputMsg);

        String input = readLine();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Название дома не может быть пустым");
        }
        return input;
    }

    /**
     * Читает {@link House#year} из консоли
     * @return год постройки дома
     */
    protected long readYear() {
        final String inputMsg = "Введите год: ";
        out.print(inputMsg);

        String input = readLine();
        return Long.parseLong(input);
    }

    /**
     * Читает {@link House#numberOfFlatsOnFloor} из консоли
     * @return кол-во квартир на этаже
     */
    protected int readNumberOfFlatsOnFloor() {
        final String inputMsg = "Введите кол-во этажей: ";
        out.print(inputMsg);

        String input = readLine();
        return Integer.parseInt(input);
    }

    /**
     * Читает {@link Flat#house} из консоли
     * @param builder {@link Flat.Builder} для записи прочитанных данных
     */
    protected void readHouse(Flat.Builder builder) {
        final String inputMsg = "Сейчас вводим дом: ";
        out.println(inputMsg);

        builder.house(new House(
                readHouseName(),
                readYear(),
                readNumberOfFlatsOnFloor()
        ));
    }

    /**
     * Читает все поля {@link Flat} из консоли
     * @return объект {@link Flat}
     */
    public Flat readFlat() {
        Flat.Builder builder = Flat.builder();

        readName(builder);
        readCoordinates(builder);
        readArea(builder);
        readNumberOfRooms(builder);
        readFurnish(builder);
        readView(builder);
        readTransport(builder);
        readHouse(builder);
        return builder.build();
    }


    public void print(Object obj) {
        out.print(obj);
    }

    public void println(Object obj) {
        out.println(obj);
    }

    public void printError(Object obj) {
        out.println(ANSI_RED + "Error: " + obj + ANSI_RESET);
    }


}
