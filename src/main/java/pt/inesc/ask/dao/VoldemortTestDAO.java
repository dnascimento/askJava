package pt.inesc.ask.dao;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.undoTracker.RUD;
import voldemort.versioning.Versioned;

// Data access object: access to database and convertion
public class VoldemortTestDAO {
    StoreClient<String, String> test;

    public VoldemortTestDAO() {
        String bootstrapUrl = "tcp://localhost:6666";
        StoreClientFactory factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
        test = factory.getStoreClient("test");
    }

    public void put(String key, String value) {
        test.put(key, value, new RUD(69L));
    }

    public String get(String key) {
        Versioned<String> versions = test.get(key, new RUD(69L));
        return versions.getValue();
    }

}
