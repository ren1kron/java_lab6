package com.vera.server.collectionManagement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vera.common.models.Flat;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

@Log
public class DumpManager {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Регистрируем модуль для поддержки LocalDateTime и других классов Java 8 даты/времени
        mapper.registerModule(new JavaTimeModule());
        // Форматирование вывода для красоты
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Метод для сохранения коллекции организаций в JSON-файл
    public static void save(Collection<Flat> flats, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), flats);
    }

    // Метод для загрузки коллекции организаций из JSON-файла
    public static Vector<Flat> load(String filePath) {
        try {
            return mapper.readValue(new File(filePath), new TypeReference<Vector<Flat>>() {
            });
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка загрузки коллекции из " + filePath, e);
            return new Vector<>();
        }
    }
}