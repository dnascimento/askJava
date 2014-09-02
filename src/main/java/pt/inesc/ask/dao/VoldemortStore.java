package pt.inesc.ask.dao;

import org.jboss.logging.Logger;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.undoTracker.SRD;
import voldemort.versioning.Version;
import voldemort.versioning.Versioned;

import com.google.protobuf.Message;

/**
 * Lazy creation voldemort store
 * 
 * @author darionascimento
 */
public class VoldemortStore<K, V extends Message> {
    private static final Logger LOG = Logger.getLogger(VoldemortStore.class.getName());

    private StoreClient<K, V> store;
    private final String storeName;
    private final String bootstrapUrl;

    public VoldemortStore(String storeName, String bootstrapUrl) {
        this.storeName = storeName;
        this.bootstrapUrl = bootstrapUrl;
    }

    private void init() {
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls(bootstrapUrl).setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS));
        store = factory.getStoreClient(storeName);
    }

    public Version put(K key, V value, SRD srd) {
        if (store == null)
            init();
        if (key == null || value == null) {
            LOG.error("Put: NULL: key" + key + " value:" + value + "t " + System.identityHashCode(this));
            // TODO throw exception
        }
        return store.put(key, value, srd);
    }

    public Versioned<V> get(K key, SRD srd) {
        if (store == null) {
            init();
        }
        if (key == null) {
            LOG.error("Get: NULL: key");
            // TODO throw exception
        }
        LOG.info("Get: " + key + " : " + srd + "t " + System.identityHashCode(this));
        return store.get(key, srd);
    }

    public boolean delete(K key, SRD srd) {
        if (store == null) {
            init();
        }
        if (key == null) {
            LOG.error("Delete: NULL: key" + "t " + System.identityHashCode(this));
            return false;
        }
        return store.delete(key, srd);
    }
}
