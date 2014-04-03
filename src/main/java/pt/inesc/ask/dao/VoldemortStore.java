package pt.inesc.ask.dao;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

/**
 * Prototype of a Voldemort Storage which includes the RID into key.
 * There is a clear limitation of using String as Key. However, its a simple prototype
 * 
 * @author darionascimento
 * @param <V>
 */
public class VoldemortStore<V> {
    private StoreClient<String, V> store;
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

    public Version put(String key, V value, long rid) {
        if (store == null)
            init();
        return store.put(key, value, rid);
    }

    public Versioned<V> get(String key, long rid) {
        if (store == null)
            init();
        return store.get(key, rid);
    }

    public boolean delete(String key, long rid) {
        if (store == null)
            init();
        return store.delete(key, rid);
    }
}
