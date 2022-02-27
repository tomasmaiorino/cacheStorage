package com.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RunningExampleCachedStorageTest {

    @Test
    public void main_NoArgumentsGiven() throws InterruptedException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RunningExampleCachedStorage.main(new String[]{});
        });
    }

    @Test
    public void startProcess_ValidInputGiven() throws InterruptedException {
        RedisStorageImpl redisStorage = new RedisStorageImpl();

        RunningExampleCachedStorage runningExampleCachedStorage = new RunningExampleCachedStorage();

        String maxCacheSize = "2";
        String [] args = new String[]{"2000", maxCacheSize , "1"};
        runningExampleCachedStorage.startProcess(args);

        Assertions.assertTrue(redisStorage.getStorageCurrentSize() <= Integer.parseInt(maxCacheSize));
    }
}
