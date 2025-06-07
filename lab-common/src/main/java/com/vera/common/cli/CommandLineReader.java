package com.vera.common.cli;


import com.vera.common.exceptions.EofException;
import com.vera.common.models.Flat;
import com.vera.common.models.House;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Отвечает за чтение их консоли
 */
public class CommandLineReader extends BaseCliReader {

    private static final String INCORRECT_MSG = "Некорректный ввод";

    public CommandLineReader(InputStream in, PrintStream out) {
        super(in, out);
    }

    /**
     * Повторяет вызов функции до тех пор, пока она не вернет корректное значение
     *
     * @param <T>  тип возвращаемого значения
     * @param f    функция, которую нужно вызвать
     * @return строка
     */
    private <T> T repeatUntilCorrect(Supplier<T> f) {

        while (true) {
            try {
                return f.get();
            } catch (EofException e) {
                throw e;
            } catch (RuntimeException e) {
                //incorrect input
                out.println(INCORRECT_MSG);
            }
        }
    }

    /**
     * Читает поле {@link Flat} из консоли
     * @param f функция чтения
     * @param builder для записи прочитанных данных
     */
    private void readUntilCorrect(Consumer<Flat.Builder> f, Flat.Builder builder) {
        while (true) {
            try {
                f.accept(builder);
                return;
            } catch (EofException e) {
                throw e;
            } catch (RuntimeException e) {
                // incorrect input
                out.println(INCORRECT_MSG);
            }
        }
    }

    /**
     * Читает поле {@link Flat#house} из консоли
     * @param builder для записи прочитанных данных
     */
    private void readHouseUntilCorrect(Flat.Builder builder) {
        builder.house(new House(
                repeatUntilCorrect(this::readHouseName),
                repeatUntilCorrect(this::readYear),
                repeatUntilCorrect(this::readNumberOfFlatsOnFloor)
        ));
    }

    /**
     * Читает поле {@link Flat#name} из консоли
     * <strong> Читает до тех пор, пока не получит правильное значение</strong>
     */
    @Override
    public Flat readFlat() {
        Flat.Builder builder = Flat.builder();
        readUntilCorrect(this::readName, builder);
        readUntilCorrect(this::readCoordinates, builder);
        readUntilCorrect(this::readArea, builder);
        readUntilCorrect(this::readNumberOfRooms, builder);
        readUntilCorrect(this::readFurnish, builder);
        readUntilCorrect(this::readView, builder);
        readUntilCorrect(this::readTransport, builder);
        readHouseUntilCorrect(builder);
        return builder.build();
    }

    /**
     * Пытается прочитать объект {@link Flat} из консоли
     * @return объект
     */
    public Flat tryToRead() {
        return super.readFlat();
    }

}
