package com.storage.model;

import com.storage.IStorage;

/**
 * Class used to simulate the process of getting an entry in the cache by using a thread.
 */
public class GetItem implements Runnable {

        private String key;
        private IStorage storage;

        public GetItem(String key, final IStorage storage) {
            this.key = key;
            this.storage = storage;
        }

        @Override
        public void run() {
            String keyFound = this.storage.get(key);
            System.out.println(String.format("Value %s found for key %s by thread %s", keyFound, key,
                    Thread.currentThread().getName()));
        }
    }