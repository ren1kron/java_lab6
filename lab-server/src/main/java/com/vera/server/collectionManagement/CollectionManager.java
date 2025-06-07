package com.vera.server.collectionManagement;

import com.vera.common.models.Flat;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class CollectionManager implements Iterable<Flat> {
    @Getter
    private final Vector<Flat> collection;

    @Getter
    private LocalDateTime lastInitTime;

    @Getter
    @Setter
    private LocalDateTime lastSaveTime;

    private final String filePath;
    private static long currentId = 1;

    public CollectionManager(String filePath) {
        this.collection = DumpManager.load(filePath);
        this.lastInitTime = LocalDateTime.now();
        this.filePath = filePath;
    }

    /**
     * Добавляет элемент в коллекцию
     *
     * @param flat элемент, который нужно добавить
     * @return true, если элемент был добавлен
     */
    public boolean add(Flat flat) {
        return collection.add(flat);
    }

    /**
     * Обновляет элемент коллекции с данным ID
     *
     * @param id   ID организации, которую нужно обновить
     * @param flat Обновлённая организация
     * @return true, если организация была успешно обновлена
     */
    public boolean update(long id, Flat flat) {
        Flat curOrg = this.byId(id);
        if (curOrg == null)
            return false;

        flat.setId(id);

        if (!collection.remove(curOrg))
            return false;

        return collection.add(flat);
    }

    /**
     * Удаляет элемент коллекции с данным ID
     * @param id ID элемента, который нужно удалить
     * @return true, если элемент был удалён
     */
    public boolean removeById(long id) {
        return collection.removeIf(org -> org.getId() == id);
    }

    /**
     * Удаляет элемент коллекции с данным индексом
     * @param index индекс элемента, который нужно удалить
     */
    public void removeByIndex(int index) {
        collection.remove(index);
    }

    public void removeGreater(Flat flat) {
        collection.removeIf(e -> flat.compareTo(e) > 0);
    }


    /**
     * Ищет в коллекции организацию по ID
     * @param id ID организации, которую нужно вернуть
     * @return Организацию с данным ID или null, если элемента с таким ID нет в коллекции
     */
    public Flat byId(long id) {
        for (Flat organization : collection) {
            if (organization.getId() == id)
                return organization;
        }
        return null;
    }

    /**
     * Находит первый не занятый ID
     * @return Свободный ID
     */
    public long getFreeId() {
        while (byId(currentId) != null)
            if (++currentId < 0)
                currentId = 1;
        return currentId;
    }

    /**
     * Сохраняет коллекцию в файл
     * @throws IOException Если произошла ошибка при сохранении
     */
    public void save() throws IOException {
        DumpManager.save(collection, filePath);
        lastSaveTime = LocalDateTime.now();
    }

    /**
     * Очищает коллекцию
     */
    public void clear() {
        collection.clear();
    }

    /**
     * @return Итератор коллекции
     */
    @Override
    public Iterator<Flat> iterator() {
        return collection.iterator();
    }
}
