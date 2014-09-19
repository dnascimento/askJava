package pt.inesc.evaluation;

import java.util.HashSet;

import org.apache.commons.lang.RandomStringUtils;

import pt.inesc.ask.dao.VoldemortStore;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.proto.AskProto;
import voldemort.undoTracker.SRD;

public class StressTest extends
        Thread {

    static String[] keys;
    static final int KEY_SIZE = 10;
    private static final int N_KEYS = 10;

    static final int THREADS = 1;

    /**
     * How many requests run counting the execution time?
     */
    static final int RUNNING_REQUESTS = 100000;
    /**
     * How many requests are used as system warm up?
     */
    static final int WARM_UP_REQUESTS = 200000;

    static final int TEXT_SIZE = 4000;

    /**
     * How many read requests are performed in 100 requests?
     */
    static final int READS_IN_100_REQUESTS = 100;

    /**
     * How many write requests are performed in 100 requests?
     */
    static final int WRITES_IN_100_REQUESTS = 0;

    private static final String DATABASE_URL = "tcp://localhost:6666";

    static String TEXT;


    @Override
    public void run() {
        VoldemortStore<String, AskProto.Comment> store = new VoldemortStore<String, AskProto.Comment>("commentStore", DATABASE_URL);
        AskProto.Comment comment = new Comment("50", TEXT, "bruteForce").getData();

        runRequests(WARM_UP_REQUESTS, true, comment, store);
        runRequests(RUNNING_REQUESTS, false, comment, store);
    }

    public void runRequests(int nRequests, boolean warmUp, AskProto.Comment comment, VoldemortStore<String, AskProto.Comment> store) {
        long start = System.nanoTime();
        for (int i = 0; i < nRequests; i++) {
            store.put(keys[i % N_KEYS], comment, new SRD(System.currentTimeMillis(), 0, false));
        }
        long duration = ((System.nanoTime() - start) / 1000000);
        System.out.println("Is warm-up: " + warmUp);
        System.out.println("Duration (miliseconds): " + duration);
        System.out.println("Rate (req/seg)" + ((double) nRequests / duration * 1000));
    }

    public static void main(String[] args) throws AskException, InterruptedException {
        System.out.println("Starting stress test");
        // create all keys to access
        HashSet<String> uniqueKey = new HashSet<String>(N_KEYS);
        keys = new String[N_KEYS];
        int i = 0;
        while (i < N_KEYS) {
            String s = RandomStringUtils.random(KEY_SIZE, true, true);
            boolean exists = uniqueKey.contains(s);
            if (!exists) {
                uniqueKey.add(s);
                keys[i++] = s;
            }
        }
        System.out.println("Keys generated");
        uniqueKey = null;

        TEXT = RandomStringUtils.random(TEXT_SIZE, true, true);



        Thread[] threads = new Thread[THREADS];
        for (i = 0; i < threads.length; i++) {
            threads[i] = new StressTest();
        }

        for (i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }
}
