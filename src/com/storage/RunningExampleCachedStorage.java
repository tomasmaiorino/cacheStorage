package com.storage;

import com.storage.model.AddItem;
import com.storage.util.GenerateThreadUtil;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunningExampleCachedStorage {

    private static final long DEFAULT_TTL = 1l;
    // Cache storage
    private RedisStorageImpl storage = new RedisStorageImpl();

    // Unique cache manager
    private CachedStore cachedStore = null;

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Missing required params");
        }

        RunningExampleCachedStorage runExample = new RunningExampleCachedStorage();
        runExample.startProcess(args);
    }

    public void startProcess(String [] args) {
        String totalEntriesToBeCreated = args[0];

        Map<String, String> entries = GenerateThreadUtil.generateRandomKeyValues(Integer.parseInt(totalEntriesToBeCreated));

        String maxCacheSize = args[1];
        // ttl cache in seconds
        String cacheTTL = null;

        if (args.length == 3) {
            cacheTTL = args[2];
        }

        long start = System.currentTimeMillis();
        putKeys(maxCacheSize, entries, cacheTTL);
        System.out.println("Execution took " + (System.currentTimeMillis() - start) +
                " ml. RedisTotalStorage: " + storage.getStorageCurrentSize());
    }

    /**
     * Method responsible for a multithread cenario where it has been created one new thread for each map entry.
     * @param maxSize cache storage max size
     * @param cacheEntries items to be added
     * @param cacheTTL cache ttl
     * @throws InterruptedException
     */
    private void putKeys(final String maxSize, Map<String, String> cacheEntries, final String cacheTTL) {
        System.out.println(String.format("Starting testing: max size: %s, total cached entries: %s, cache ttl: %s",
                maxSize, cacheEntries.size(), cacheTTL));

        CachedStore cachedStore = new CachedStore(storage, Integer.parseInt(maxSize),
                Objects.nonNull(cacheTTL) ? Long.parseLong(cacheTTL) : DEFAULT_TTL);

       // It has being used twice the size of the cacheEntries due the get key tests
        ExecutorService executor = Executors.newFixedThreadPool(cacheEntries.size() + (cacheEntries.size() / 2));

        // There keys will be used by the get threads
        List<String> keys = cacheEntries.keySet().stream().toList();

        cacheEntries.forEach((k, v) -> {
            Runnable putThread = new AddItem(cachedStore, k, v);
            executor.execute(putThread);

            // these threads are being created in order to simulate a cache request
            Runnable readThread = GenerateThreadUtil.createReadThread(keys, cacheEntries, cachedStore);

            executor.execute(readThread);
        });

        executor.shutdown();
        System.out.println("Finished all put threads");
    }
}
