package com.storage;

import com.storage.util.GenerateThreadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;

public class CachedStoreTest {

    @Test
    public void put_ValidKeyGiven() {
        // The right approach would be to create a mock for RedisStorageImpl class.
        RedisStorageImpl redisStorage = new RedisStorageImpl();
        CachedStore cachedStore = new CachedStore(redisStorage, 5, 1);
        String key = "123456";
        String value = UUID.randomUUID().toString();

        cachedStore.put(key, value);

        Assertions.assertTrue(redisStorage.getStorageCurrentSize() == 1);
        Assertions.assertTrue(redisStorage.storage.containsKey(key));
    }

    @Test
    public void put_MoreThanMaxItemsGiven() {
        // The right approach would be to create a mock for RedisStorageImpl class.
        RedisStorageImpl redisStorage = new RedisStorageImpl();
        int maxCacheSize = 5;
        CachedStore cachedStore = new CachedStore(redisStorage, maxCacheSize, 1);

        Map<String, String> entries = GenerateThreadUtil.generateRandomKeyValues(20);
        entries.forEach((k, v) -> {
            cachedStore.put(k, v);
        });

        Assertions.assertTrue(redisStorage.getStorageCurrentSize() <= maxCacheSize);
    }

    @Test
    public void checkingTTL() throws InterruptedException {
        // The right approach would be to create a mock for RedisStorageImpl class.
        RedisStorageImpl redisStorage = new RedisStorageImpl();
        int maxCacheSize = 5;
        CachedStore cachedStore = new CachedStore(redisStorage, maxCacheSize, 1);

        String firstKey = UUID.randomUUID().toString();
        String firstValue = UUID.randomUUID().toString();
        cachedStore.put(firstKey, firstValue);

        Map<String, String> entries = GenerateThreadUtil.generateRandomKeyValues(20);

        int cont = 0;
        for(Map.Entry<String, String> entry : entries.entrySet()) {
            cachedStore.put(entry.getKey(), entry.getValue());
            // it stops the put process in the middle and sleep the current thread in order to give the TTL time to expire
            if (cont++ == 10) {
                Thread.sleep(1000);
            }
        }

        Assertions.assertFalse(redisStorage.storage.containsKey(firstKey));
        Assertions.assertTrue(redisStorage.getStorageCurrentSize() <= maxCacheSize);
    }

    @Test
    public void get_FoundKeyGiven() {
        RedisStorageImpl redisStorage = new RedisStorageImpl();
        CachedStore cachedStore = new CachedStore(redisStorage, 5, 1);
        String key = "123456";
        String value = UUID.randomUUID().toString();
        cachedStore.put(key, value);

        String keyValue = cachedStore.get(key);

        Assertions.assertEquals(keyValue, value);
    }

    @Test
    public void get_NotFoundKeyGiven() {
        RedisStorageImpl redisStorage = new RedisStorageImpl();
        CachedStore cachedStore = new CachedStore(redisStorage, 5, 1);
        String key = "123456";

        String keyValue = cachedStore.get(key);

        Assertions.assertNull(keyValue);
    }
}
