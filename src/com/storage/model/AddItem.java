package com.storage.model;

import com.storage.CachedStore;


/**
 * Class used to simulate the process of adding an entry in the cache by using a thread.
 */
public class AddItem implements Runnable {

        private CachedStore cachedStore = null;
        private String key;
        private String value;

        public AddItem(CachedStore pCachedStore, String pKey, String pValue) {
            cachedStore = pCachedStore;
            key = pKey;
            value = pValue;
        }

        @Override
        public void run() {
            cachedStore.put(key, value);
        }
    }