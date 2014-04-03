package pt.inesc.ask.dao;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

import com.google.protobuf.Message;

/**
 * Lazy creation voldemort store
 * 
 * @author darionascimento
 */
public class VoldemortStore<K, V extends Message> {
    private StoreClient<K, V> store;
    private final String storeName;
    private final String bootstrapUrl;

    public VoldemortStore(String storeName, String bootstrapUrl) {
        this.storeName = storeName;
        this.bootstrapUrl = bootstrapUrl;
    }

    private void init() {
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls(bootstrapUrl)
                                  .setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS));
        store = factory.getStoreClient(storeName);
    }

    public Version put(K key, V value, long rid) {
        if (store == null)
            init();
        return store.put(key, value, rid);
    }

    public Versioned<V> get(K key, long rid) {
        if (store == null)
            init();
        System.out.println("Get: " + key + " : " + rid);
        return store.get(key, rid);
    }

    public boolean delete(K key, long rid) {
        if (store == null)
            init();
        return store.delete(key, rid);
    }

}
