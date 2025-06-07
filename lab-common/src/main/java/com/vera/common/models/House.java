package com.vera.common.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий дом
 */
public class House implements Serializable {
    private String name; //Поле не может быть null
    private long year; //Значение поля должно быть больше 0
    private Integer numberOfFlatsOnFloor; //Значение поля должно быть больше 0

    public House(String name, long year, Integer numberOfFlatsOnFloor) {

        Objects.requireNonNull(name, "name must not be null");

        if (year < 0) {
            throw new IllegalArgumentException("Year must not be negative");
        }

        if (numberOfFlatsOnFloor < 0) {
            throw new IllegalArgumentException("Number of flats must not be negative");
        }

        this.name = name;
        this.year = year;
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
    }

    public String getName() {
        return name;
    }

    public long getYear() {
        return year;
    }

    public Integer getNumberOfFlatsOnFloor() {
        return numberOfFlatsOnFloor;
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", numberOfFlatsOnFloor=" + numberOfFlatsOnFloor +
                '}';
    }
}
