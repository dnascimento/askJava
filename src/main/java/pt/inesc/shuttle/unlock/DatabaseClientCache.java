package pt.inesc.shuttle.unlock;

import java.util.concurrent.ConcurrentHashMap;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.utils.ByteArray;

public class DatabaseClientCache {

    ConcurrentHashMap<String, StoreClient<ByteArray, Object>> cache;


    public DatabaseClientCache() {
        cache = new ConcurrentHashMap<String, StoreClient<ByteArray, Object>>();
    }

    public StoreClient<ByteArray, Object> get(String storeName) {
        StoreClient<ByteArray, Object> s = cache.get(storeName);
        if (s == null) {
            newClient(storeName);
        }
        return cache.get(storeName);

    }

    private void newClient(String storeName) {
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls("tcp://localhost:6666")
                                  .setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS));
        StoreClient<ByteArray, Object> s = factory.getStoreClient(storeName);
        cache.putIfAbsent(storeName, s);
    }

}
