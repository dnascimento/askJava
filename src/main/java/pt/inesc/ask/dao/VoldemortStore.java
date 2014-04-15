package pt.inesc.ask.dao;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.undoTracker.RUD;
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

    public Version put(K key, V value, RUD rud) {
        if (store == null)
            init();
        if (key == null || value == null) {
            System.err.println("Put: NULL: key" + key + " value:" + value + "t " + System.identityHashCode(this));
            // TODO throw exception
        }
        return store.put(key, value, rud);
    }

    public Versioned<V> get(K key, RUD rud) {
        if (store == null)
            init();
        if (key == null) {
            System.err.println("Get: NULL: key");
            // TODO throw exception
        }
        System.out.println("Get: " + key + " : " + rud + "t " + System.identityHashCode(this));
        return store.get(key, rud);
    }

    public boolean delete(K key, RUD rud) {
        if (store == null)
            init();
        if (key == null) {
            System.err.println("Delete: NULL: key" + "t " + System.identityHashCode(this));
            return false;
        }
        return store.delete(key, rud);
    }
}
