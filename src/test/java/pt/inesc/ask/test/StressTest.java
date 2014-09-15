package pt.inesc.ask.test;

import java.util.Arrays;

import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Question;
import voldemort.undoTracker.SRD;

public class StressTest extends
        Thread {
    private static final int THREADS = 10;
    static int DURATION = 100000;
    private final String key;

    public StressTest(String key) {
        this.key = key;
    }


    @Override
    public void run() {
        VoldemortDAO dao = new VoldemortDAO("tcp://localhost:6666");
        SRD srd = new SRD(30, 0, false);
        Question quest = new Question(key, Arrays.asList("dario"), "1000", "1", "");
        long start = System.nanoTime();
        for (int i = 0; i < DURATION; i++) {
            dao.save(quest, srd);
        }
        long duration = ((System.nanoTime() - start) / 1000000);
        System.out.println("Duration (miliseconds): " + duration);
        System.out.println("Rate (req/seg)" + ((double) DURATION / duration * 1000));

        System.out.println("Get");
        start = System.nanoTime();
        for (int i = 0; i < DURATION; i++) {
            try {
                dao.getQuestion(key, srd);
            } catch (AskException e) {
                e.printStackTrace();
            }
        }
        duration = ((System.nanoTime() - start) / 1000000);
        System.out.println("Duration (miliseconds): " + duration);
        System.out.println("Rate (req/seg)" + ((double) DURATION / duration * 1000));
    }

    public static void main(String[] args) throws AskException, InterruptedException {
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new StressTest("DARIO" + i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }
}
