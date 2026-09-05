package com.airtribe.meditrack.util;

import java.util.*;
import java.util.function.Function;

public class DataStore<T> {

    private final Map<String, T> records = new LinkedHashMap<>();
    private final Function<T, String> idExtractor;

    public DataStore(Function<T, String> idExtractor) {
        this.idExtractor = idExtractor;
    }

    public void add(T item) {
        records.put(idExtractor.apply(item), item);
    }

    public T getById(String id) {
        return records.get(id);
    }

    public boolean delete(String id) {
        return records.remove(id) != null;
    }

    public boolean exists(String id) {
        return records.containsKey(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(records.values());
    }

    public Collection<T> values() {
        return records.values();
    }

    public int size() {
        return records.size();
    }

    public boolean update(T item) {
        String id = idExtractor.apply(item);
        if (!records.containsKey(id)) {
            return false;
        }
        records.put(id, item);
        return true;
    }
}
