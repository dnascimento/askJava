package pt.inesc.ask.dao;

import java.util.concurrent.TimeUnit;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.protocol.RequestFormatType;
import voldemort.store.nonblockingstore.NonblockingStore;

public class VoldemortNonBlocking {

    NonblockingStore store;

    public VoldemortNonBlocking(String storeName, String bootstrapUrl) {
        SocketStoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls(bootstrapUrl)
                                  .setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS)
                                  .setEnableJmx(true)
                                  .setSocketKeepAlive(true)
                                  .setMaxBootstrapRetries(20)
                                  .setConnectionTimeout(20000, TimeUnit.MILLISECONDS)
                                  .setFailureDetectorAsyncRecoveryInterval(200)
                                  .setFailureDetectorThresholdCountMinimum(20)
                                  .setFailureDetectorRequestLengthThreshold(10000)
                                  .setSocketTimeout(20000, TimeUnit.MILLISECONDS));
        StoreClient<Object, Object> store = factory.getStoreClient(storeName);
    }
}
