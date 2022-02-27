package com.storage;

import java.util.HashMap;
import java.util.Map;

public class RedisStorageImpl implements IStorage {

    final Map<String, String> storage = new HashMap<>();

    @Override
    public void put(String key, String value) {
        storage.put(key, value);
        System.out.println("RedisStorage: Adding key " + key + " .Value " + value + " .Current size " + storage.size());
    }

    @Override
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public void remove(String key) {
        System.out.println("RedisStorage: removing key " + key + ". Current size " + storage.size());
        storage.remove(key);
    }

    public int getStorageCurrentSize() {
        return storage.size();
    }
}
