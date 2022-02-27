package com.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunningExampleCachedStorage {

    RedisStorageImpl storage = new RedisStorageImpl();
    CachedStore cachedStore = null;

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Missing required params");
        }

        RunningExampleCachedStorage runExample = new RunningExampleCachedStorage();
        String filePath = args[0];

        Map<String, String> entries = runExample.getFileEntries(filePath);

        String maxCacheSize = args[1];
        String cacheTTL = null;

        if (args.length == 3) {
            cacheTTL = args[2];
        }
        long start = System.currentTimeMillis();
        runExample.putKeys(maxCacheSize, entries, cacheTTL);
        System.out.println("The all test took " + (System.currentTimeMillis() - start));
    }

    private Map<String, String> getFileEntries(String filePath) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filePath));
        Map<String, String> entries = new HashMap<>();

        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split(",");
            entries.put(line[0], line[1]);
        }

        return entries;
    }

    private void putKeys(final String maxSize, Map<String, String> cacheEntries, final String cacheTTL) throws InterruptedException {
        System.out.println(String.format("Starting testing: max size: %s, total cached entries: %s, cache ttl: %s", maxSize, cacheEntries.size(), cacheTTL));

        CachedStore cachedStore = new CachedStore(storage, Integer.parseInt(maxSize),
                Objects.nonNull(cacheTTL) ? Long.parseLong(cacheTTL) : 1);
       // It has being used twice the size of the cacheEntries due the get key tests
        ExecutorService executor = Executors.newFixedThreadPool(cacheEntries.size() * 2);

        List<String> keys = cacheEntries.keySet().stream().toList();

        cacheEntries.forEach((k, v) -> {
            Runnable putThread = new AddItem(cachedStore, k, v);
            executor.execute(putThread);
            Runnable readThread = createReadThread(keys, cacheEntries, cachedStore);
            executor.execute(readThread);
        });

        executor.shutdown();

        while (!executor.isTerminated()) {
            // empty body
        }
        System.out.println("\nFinished all put threads");
    }

    private GetItem createReadThread(List<String> keys, Map<String, String> cacheEntries, CachedStore cachedStore) {
        int randomKey = (int) ((Math.random() * (cacheEntries.size() - 0)) + 0);
        // Here we'll use a random index to get a key to be searched
        return new GetItem(keys.get(randomKey), storage);
    }

    private class GetItem implements Runnable {
        private String key;
        private IStorage storage;

        public GetItem(String key, final IStorage storage) {
            this.key = key;
            this.storage = storage;
        }

        @Override
        public void run() {
            String keyFound = this.storage.get(key);
            System.out.println(String.format("Value %s found for key %s by thread %s", keyFound, key, Thread.currentThread().getName()));
        }
    }

    private class AddItem implements Runnable {

        private CachedStore cachedStore = null;
        private String key;
        private String value;
        private Long ttl;

        public AddItem(CachedStore pCachedStore, String pKey, String pValue) {
            cachedStore = pCachedStore;
            key = pKey;
            value = pValue;
        }

        @Override
        public void run() {
            if (Objects.nonNull(ttl)) {
                cachedStore.put(key, value, ttl);
            } else {
                cachedStore.put(key, value);
            }
        }
    }
}
