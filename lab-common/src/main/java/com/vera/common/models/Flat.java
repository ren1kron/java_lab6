package com.vera.common.models;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Класс, представляющий квартиру
 */
@Getter
public class Flat implements Comparable<Flat>, Serializable {

    /**
     * Вложенный класс, реализующий паттерн Builder
     */
    public static class Builder {

        private String name; //Поле не может быть null, Строка не может быть пустой
        private Coordinates coordinates; //Поле не может быть null
        private Float area; //Максимальное значение поля: 525, Значение поля должно быть больше 0
        private Long numberOfRooms; //Значение поля должно быть больше 0
        private Furnish furnish; //Поле не может быть null
        private View view; //Поле может быть null
        private Transport transport; //Поле не может быть null
        private House house;//Поле может быть null


        public Builder name(String name) {
            if (name.isBlank()) {
                throw new IllegalArgumentException("Name must not be empty");
            }
            this.name = name;
            return this;
        }

        public Builder coordinates(Coordinates coordinates) {
            Objects.requireNonNull(coordinates, "coordinates must not be null");
            this.coordinates = coordinates;
            return this;
        }

        public Builder area(Float area) {
            if (area < 0 || area > 525) {
                throw new IllegalArgumentException("Area out of range");
            }
            this.area = area;
            return this;
        }

        public Builder numberOfRooms(Long numberOfRooms) {
            if (numberOfRooms  < 0) {
                throw new IllegalArgumentException("Number of rooms must not be negative");
            }
            this.numberOfRooms = numberOfRooms;
            return this;
        }

        public Builder furnish(Furnish furnish) {
            Objects.requireNonNull(furnish, "furnish must not be null");
            this.furnish = furnish;
            return this;
        }

        public Builder view(View view) {
            Objects.requireNonNull(view, "view must not be null");
            this.view = view;
            return this;
        }

        public Builder transport(Transport transport) {
            Objects.requireNonNull(transport, "transport must not be null");
            this.transport = transport;
            return this;
        }

        public Builder house(House house) {
            this.house = house;
            return this;
        }

        public Flat build() {
            return new Flat(name, coordinates, area, numberOfRooms, furnish, view, transport, house);
        }
    }


    public static Builder builder() {
        return new Builder();
    }



    @Setter
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float area; //Максимальное значение поля: 525, Значение поля должно быть больше 0
    private Long numberOfRooms; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле не может быть null
    private View view; //Поле может быть null
    private Transport transport; //Поле не может быть null
    private House house;//Поле может быть null


    private Flat(String name, Coordinates coordinates, Float area, Long numberOfRooms, Furnish furnish, View view,
                 Transport transport, House house) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = new Date();
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.furnish = furnish;
        this.view = view;
        this.transport = transport;
        this.house = house;
    }

    @Override
    public int compareTo(Flat o) {
        return Float.compare(this.area, o.area);
    }

    public void mergeWith(Flat flat) {
        this.name = flat.name;
        this.coordinates = flat.coordinates;
        this.area = flat.area;
        this.numberOfRooms = flat.numberOfRooms;
        this.furnish = flat.furnish;
        this.view = flat.view;
        this.transport = flat.transport;
        this.house = flat.house;
    }

    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", numberOfRooms=" + numberOfRooms +
                ", furnish=" + furnish +
                ", view=" + view +
                ", transport=" + transport +
                ", house=" + house +
                '}';
    }
}





