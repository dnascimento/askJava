package pt.inesc.evaluation;

import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import pt.inesc.ask.dao.VoldemortStore;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.proto.AskProto;
import voldemort.undoTracker.SRD;

public class StressTest extends
        Thread {

    static String[] keys;
    static final int KEY_SIZE = 10;
    private static final int N_KEYS = 1000;

    static final int THREADS = 8;
    static final boolean UNIQUE_KEY = false;
    static long start = Long.MAX_VALUE;
    static long end = 0L;

    /**
     * How many requests run counting the execution time?
     */
    static final int RUNNING_REQUESTS = 100000;
    /**
     * How many requests are used as system warm up?
     */
    static final int WARM_UP_REQUESTS = 50000;

    static final int TEXT_SIZE = 4000;


    private static final String DATABASE_URL = "tcp://database:6666";
    static VoldemortStore<String, AskProto.Comment> store = new VoldemortStore<String, AskProto.Comment>("commentStore", DATABASE_URL);

    static String TEXT;
    private final String id;


    public StressTest(int id) {
        String id_tmp = "a";
        for (int i = 0; i < id; i++) {
            id_tmp += "a";
        }
        this.id = id_tmp;

    }


    @Override
    public void run() {
        System.out.println("Run thread " + id);
        AskProto.Comment comment = new Comment("50", TEXT, "bruteForce").getData();

        runRequests(WARM_UP_REQUESTS, true, comment, store);
        runRequests(RUNNING_REQUESTS, false, comment, store);
    }

    public synchronized static void setStart(long attempt) {
        if (attempt < start) {
            start = attempt;
        }
    }

    public synchronized static void setEnd(long attempt) {
        if (attempt > end) {
            end = attempt;
        }
    }


    public void runRequests(int nRequests, boolean warmUp, AskProto.Comment comment, VoldemortStore<String, AskProto.Comment> store) {
        if (!warmUp) {
            StressTest.setStart(System.currentTimeMillis());
        }
        long startThread = System.nanoTime();

        Random r = new Random();
        for (int i = 0; i < nRequests; i++) {
            try {
                if (false) { // 20% -> 4
                    if (UNIQUE_KEY) {
                        store.put(id, comment, new SRD(System.currentTimeMillis(), 0, false));
                    } else {
                        store.put(keys[r.nextInt(N_KEYS)], comment, new SRD(System.currentTimeMillis(), 0, false));
                    }
                } else {
                    if (UNIQUE_KEY) {
                        store.get(id, new SRD(System.currentTimeMillis(), 0, false));
                    } else {
                        store.get(keys[r.nextInt(N_KEYS)], new SRD(System.currentTimeMillis(), 0, false));
                    }
                }
            } catch (Exception e) {

            }
        }
        long duration = ((System.nanoTime() - startThread) / 1000000);
        if (!warmUp) {
            StressTest.setEnd(System.currentTimeMillis());
        }
        System.out.println("Is warm-up: " + warmUp + "thread id: " + id);
        System.out.println("Is warm-up: " + warmUp);
        System.out.println("Duration (miliseconds): " + duration);
        System.out.println("Rate (req/seg)" + ((double) nRequests / duration * 1000));
    }

    public static void main(String[] args) {
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


        AskProto.Comment comment = new Comment("50", TEXT, "bruteForce").getData();
        if (UNIQUE_KEY) {
            for (int id = 0; id < threads.length; id++) {
                String id_tmp = "a";
                for (int k = 0; k < id; k++) {
                    id_tmp += "a";
                }
                store.put(id_tmp, comment, new SRD(System.currentTimeMillis(), 0, false));
            }
        } else {
            for (i = 0; i < keys.length; i++) {
                store.put(keys[i], comment, new SRD(System.currentTimeMillis(), 0, false));
            }
        }


        for (i = 0; i < threads.length; i++) {
            threads[i] = new StressTest(i);
        }

        for (i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long duration = end - start;
        System.out.println("TOTAL: Duration (miliseconds): " + duration);
        System.out.println("TOTAL: Rate (req/seg)" + ((double) THREADS * (RUNNING_REQUESTS) / duration) * 1000);

    }
}
