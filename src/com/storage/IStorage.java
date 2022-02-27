package com.storage;

public interface IStorage {

    void put(String key, String value);
    void put(String key, String value, long ttl);
    String get(String key);
    void remove(String key);
}
