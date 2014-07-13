package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.inesc.ask.proto.AskProto;
import pt.inesc.shuttle.CassandraClient;
import pt.inesc.shuttle.ShuttleInterceptor;
import pt.inesc.shuttle.VoldemortUnlocker;
import voldemort.client.StoreClient;
import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.RUD;
import voldemort.undoTracker.RUD.OpType;
import voldemort.utils.ByteArray;

import com.google.common.collect.ArrayListMultimap;

public class UnlockTest {

    StoreClient<ByteArray, AskProto.Question> store;

    public UnlockTest() {
        // StoreClientFactory factory = new SocketStoreClientFactory(
        // new
        // ClientConfig().setBootstrapUrls("tcp://localhost:6666").setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS));
        // store = factory.getStoreClient("questionStore");
    }

    @Test
    public void unlockTest() {
        ByteArray key = new ByteArray("dario".getBytes());
        ByteArray key1 = new ByteArray("dario1".getBytes());

        // Unlocking algorithm
        ArrayListMultimap<ByteArray, KeyAccess> accessedKeys = ArrayListMultimap.create();
        accessedKeys.put(key, new KeyAccess("index", OpType.Put, 4));
        accessedKeys.put(key, new KeyAccess("questions", OpType.Get, 2));
        accessedKeys.put(key1, new KeyAccess("answer", OpType.Put, 1));

        CassandraClient cassandra = new CassandraClient();

        cassandra.addKeys(accessedKeys, 123L);
        ArrayListMultimap<ByteArray, KeyAccess> storedKeys = cassandra.getKeys(123L);
        assertEquals(accessedKeys, storedKeys);
    }


    @Test
    public void listSubtraction() {
        ShuttleInterceptor i = new ShuttleInterceptor();
        ByteArray key = new ByteArray("dario".getBytes());

        ArrayListMultimap<ByteArray, KeyAccess> originalTable = ArrayListMultimap.create();
        originalTable.put(key, new KeyAccess("index", OpType.Put, 4));
        originalTable.put(key, new KeyAccess("questions", OpType.Get, 2));
        originalTable.put(key, new KeyAccess("answer", OpType.Get, 2));

        ArrayListMultimap<ByteArray, KeyAccess> accessedKeys = ArrayListMultimap.create();
        accessedKeys.put(key, new KeyAccess("index", OpType.Put, 1));
        accessedKeys.put(key, new KeyAccess("questions", OpType.Get, 2));

        i.subtrackTables(accessedKeys, originalTable);
    }

    @Test
    public void unlocking() {
        VoldemortUnlocker unlock = new VoldemortUnlocker();
        CassandraClient cassandra = new CassandraClient();
        ArrayListMultimap<ByteArray, KeyAccess> storedKeys = cassandra.getKeys(1405247788390L);

        unlock.unlockKeys(storedKeys, new RUD(1405247788390L));
    }

}
