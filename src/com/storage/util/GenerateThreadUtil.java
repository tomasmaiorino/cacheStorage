package com.storage.util;

import com.storage.IStorage;
import com.storage.model.GetItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GenerateThreadUtil {

    public static GetItem createReadThread(final List<String> keys, final Map<String, String> cacheEntries,
                                           final IStorage storage) {
        int randomKey = (int) ((Math.random() * ((cacheEntries.size() / 2) - 0)) + 0);
        // Here we'll use a random index to get a key to be searched
        return new GetItem(keys.get(randomKey), storage);
    }

    public static Map<String, String> getFileEntries(String filePath) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filePath));
        Map<String, String> entries = new HashMap<>();

        while (sc.hasNextLine()) {
            String[] line = sc.nextLine().split(",");
            entries.put(line[0], line[1]);
        }
        return entries;
    }

    public static Map<String, String> generateRandomKeyValues(int size) {
        Map<String, String> entries = new HashMap<>(size);

        while (size > 0 ) {
            entries.put(UUID.randomUUID().toString().replace("-", "").substring(0, 9), UUID.randomUUID().toString());
            size--;
        }
        return entries;
    }
}
