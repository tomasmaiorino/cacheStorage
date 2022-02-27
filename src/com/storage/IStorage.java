package com.storage;

public interface IStorage {

    void put(String key, String value);
    String get(String key);
    void remove(String key);
}
