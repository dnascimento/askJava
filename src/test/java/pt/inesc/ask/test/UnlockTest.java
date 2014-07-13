package pt.inesc.ask.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
        RUD reqOriginal = new RUD(1000L);
        reqOriginal.addAccessedKey(key, "index", OpType.Put);
        reqOriginal.addAccessedKey(key, "index", OpType.Put);
        reqOriginal.addAccessedKey(key, "index", OpType.Put);
        reqOriginal.addAccessedKey(key, "index", OpType.Put);

        reqOriginal.addAccessedKey(key, "questions", OpType.Get);
        reqOriginal.addAccessedKey(key, "questions", OpType.Get);

        reqOriginal.addAccessedKey(key, "answer", OpType.Get);
        reqOriginal.addAccessedKey(key, "answer", OpType.Get);

        ArrayListMultimap<ByteArray, KeyAccess> originalTable = reqOriginal.getAccessedKeys();

        RUD reqNew = new RUD(1002L);
        reqNew.addAccessedKey(key, "index", OpType.Put);
        reqNew.addAccessedKey(key, "index", OpType.Put);

        reqNew.addAccessedKey(key, "questions", OpType.Get);
        reqNew.addAccessedKey(key, "questions", OpType.Get);


        ArrayListMultimap<ByteArray, KeyAccess> accessedKeys = reqNew.getAccessedKeys();



        i.subtrackTables(accessedKeys, originalTable);

        ArrayListMultimap<ByteArray, KeyAccess> expected = ArrayListMultimap.create();
        expected.put(key, new KeyAccess("index", OpType.Put, 2));
        expected.put(key, new KeyAccess("answer", OpType.Get, 2));


        assertEquals(expected.keys(), accessedKeys.keys());
        // same entries
        assertEquals(expected.values().size(), accessedKeys.values().size());
        // same times:

        for (ByteArray k : expected.keySet()) {
            List<KeyAccess> newList = expected.get(k);
            List<KeyAccess> originalList = accessedKeys.get(k);
            assertTrue(compareList(newList, originalList));
        }
    }



    private boolean compareList(List<KeyAccess> newList, List<KeyAccess> originalList) {
        for (KeyAccess oAccess : originalList) {
            int index = newList.indexOf(oAccess);
            KeyAccess newAccess = newList.get(index);
            if (oAccess.times != newAccess.times)
                return false;
        }
        return true;
    }

    @Test
    public void unlocking() {
        VoldemortUnlocker unlock = new VoldemortUnlocker();
        CassandraClient cassandra = new CassandraClient();
        ArrayListMultimap<ByteArray, KeyAccess> storedKeys = cassandra.getKeys(1405247788390L);

        unlock.unlockKeys(storedKeys, new RUD(1405247788390L));
    }

}
