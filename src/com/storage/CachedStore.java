package com.storage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedStore implements IStorage {

    private int maxSize;
    private final IStorage storage;
    private final Map<String, Long> ttlMapping = new ConcurrentHashMap<>();
    private int currentSize = 0;
    private long cacheTTL;

    public CachedStore(IStorage storage, int maxSize, long cacheTTL) {
        this.storage = storage;
        this.maxSize = maxSize;
        this.cacheTTL = cacheTTL;
    }

    @Override
    public void put(String key, String value) {
        // I'm using a synchronized block and not a synchronized method because so far this is the only place
        // we need to worry about multiple threads.
        synchronized (storage) {
            System.out.println(String.format("CachedStorage: Checking size before add key %s. Current size %s max size %s",
                    key, currentSize, maxSize));
            if (currentSize == maxSize) {
                // This is being done only here in order to avoid a ttl check all requests once the ttl method
                // would iterate through all items in the ttl mapping
                //
                checkTTL();
                // Tries again to add the item
                if (currentSize == maxSize) {
                    System.out.println("CachedStorage: Too many keys. Thread " + Thread.currentThread().getName());
                    return;
                }
            }
            System.out.println("CachedStorage: adding key " + key + ". For Thread " + Thread.currentThread().getName());
            storage.put(key, value);
            ttlMapping.put(key, getConfiguredTTL(cacheTTL));
            currentSize++;
        }
    }

    private long getConfiguredTTL(long ttl) {
        return LocalDateTime.now().plusSeconds(ttl).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Method responsible for checking whether the TTL time had expired.
     */
    private void checkTTL() {
        System.out.println("CachedStore: Total items before checkTTL: " + currentSize + " .Thread " + Thread.currentThread().getName());
        ttlMapping.forEach((k, v) -> {
           // System.out.println(String.format("key %s, value %s, Current time %s . Thread %s", k, v, System.currentTimeMillis(), Thread.currentThread().getName()));
            if (v <= System.currentTimeMillis()) {
                System.out.println("CachedStorage: TTL for key " + k);
                ttlMapping.remove(k);
                remove(k);
            }
        });
        System.out.println("CachedStore: Total items after checkTTL: " + currentSize + " .Thread " + Thread.currentThread().getName());
    }

    // As this method only gets data from the cache, there is no need to made id thread safe.
    public String get(String key) {
        return storage.get(key);
    }

    @Override
    public void remove(String key) {
        storage.remove(key);
        currentSize--;
    }
}