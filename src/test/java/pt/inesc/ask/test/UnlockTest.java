package pt.inesc.ask.test;

import org.junit.Test;

import pt.inesc.ask.proto.AskProto;
import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.utils.ByteArray;

public class UnlockTest {

    StoreClient<ByteArray, AskProto.Question> store;

    public UnlockTest() {
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls("tcp://localhost:6666")
                                  .setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS));
        store = factory.getStoreClient("questionStore");
    }

    @Test
    public void unlockTest() {
        // TODO refazer o teste
        // List<ByteArray> keys = Arrays.asList(new ByteArray("dario".getBytes()), new
        // ByteArray("ze".getBytes()));
        // Map<ByteArray, Boolean> result = store.unlockKeys(keys, new RUD(69L));
        // assertEquals(keys.size(), result.size());
        // for (Boolean v : result.values()) {
        // assertTrue(v);
        // }
    }

}
