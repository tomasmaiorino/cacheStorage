# CacheStorage
When running this application it would be simulated requests to a cache storage by using a cache manager.

The cache storage is responsible for store the cache entries through a hashmap. 

The cache manager is responsible for the following:

1 - To control the cache's size by not allowing the cache to have entries than the max size.

2 - To manage the entries' TTL and remove it from the cache when the TTL expired.

3 - To return a specific key when required.

The first parameter is the amount of entries that will be created and added to the cache if the cache has not reached its limit.
For each entry a new thread will be created only to try to add the specific key and value in the cache. 
This was done this way to try to simulate a web request, which uses a new thread for each request. 

##Steps to run the application (Unix | MacOS)

1 - Download the source code from [link github](https://github.com/tomasmaiorino/cacheStorage).

2 - Enter the application folder and compile the application running the following command.

```$
javac $(find ./src/* | grep .java) -d out
```

3 - Run the application using the following command:

```$
java -cp $(pwd)/out com.storage.RunningExampleCachedStorage 2000 4 2
```
The command above requires three parameters:

3.1 - The amount of items to be created (number). Remember a thread will be created for each entry.

3.2 - The max items in the cache storage (number). 

3.3 - The ttl for all entries in seconds (number).

###Notes
* You can also import the application in our IDE.
* 