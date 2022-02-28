# Cache Storage
This application simulates requests to a cache storage which is managed by a cache manager.
The application needs three parameters: Total entries, max size and TTL. 

For each entry a new thread will be created and this thread will try to add a new entry in the cache. 
The application also does some get requests simulation and for them a new thread is used for each request as well. By checking the log information we can verify the application's behavior.

The cache storage stores the cache entries in a hashmap. 

The cache manager is responsible for the following:

1 - To control the cache's size by not allowing the cache to have more entries than the max size.

2 - To manage the entries' TTL and remove it from the cache when the TTL expired.

3 - To return a specific entry when required by using the key.


##Steps to run the application (Unix | MacOS)

1 - Download the source code from [link github](https://github.com/tomasmaiorino/cacheStorage).

2 - Enter the application folder and compile the application running the following command.

```$
javac $(find ./src/* | grep .java) -d out
```

3 - Run the application within the project folder using the following command :

```$
java -cp $(pwd)/out com.storage.RunningExampleCachedStorage param1 param2 param3
```
The command above must have three parameters:

3.1 - The amount of items to be created (number). Remember a new thread will be created for each entry.

3.2 - The max items in the cache storage (number). 

3.3 - The ttl for all entries in seconds (number).

e.g:
```$
java -cp $(pwd)/out com.storage.RunningExampleCachedStorage 2000 100 1
```

###Notes
* You can also import the application in our IDE and execute the code from there.
