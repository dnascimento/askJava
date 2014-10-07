package pt.inesc;

import java.util.concurrent.TimeUnit;

import pt.inesc.ask.proto.AskProto;
import pt.inesc.ask.proto.AskProto.Index;
import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClient;
import voldemort.client.StoreClientFactory;
import voldemort.client.protocol.RequestFormatType;
import voldemort.undoTracker.SRD;
import voldemort.versioning.Versioned;

public class ConcurrentUpdate extends
        Thread {
    private static final String KEY = "dario";
    private static final int N_THREADS = 20;
    private static final int N_TIMES = 1000;


    private final int countTimes;
    private final StoreClient<String, AskProto.Index> store;

    public ConcurrentUpdate(int countTimes) {
        this.countTimes = countTimes;
        StoreClientFactory factory = new SocketStoreClientFactory(
                new ClientConfig().setBootstrapUrls("tcp://localhost:6666")
                                  .setRequestFormatType(RequestFormatType.PROTOCOL_BUFFERS)
                                  .setEnableJmx(true)
                                  .setSocketKeepAlive(true)
                                  .setMaxBootstrapRetries(20)
                                  .setConnectionTimeout(20000, TimeUnit.MILLISECONDS)
                                  .setFailureDetectorAsyncRecoveryInterval(200)
                                  .setFailureDetectorThresholdCountMinimum(20)
                                  .setFailureDetectorRequestLengthThreshold(10000)
                                  .setSocketTimeout(20000, TimeUnit.MILLISECONDS));
        store = factory.getStoreClient("index");
    }


    @Override
    public void run() {
        for (int i = 0; i < countTimes; i++) {
            Versioned<Index> versioned = null;
            try {
                System.out.println(i);
                versioned = store.get(KEY, new SRD());
                if (versioned == null) {
                    versioned = new Versioned<AskProto.Index>(Index.newBuilder().addEntry("vai").build());
                } else {
                    versioned.setObject(Index.newBuilder().addAllEntry(versioned.getValue().getEntryList()).addEntry("vai").build());
                }
                store.put(KEY, versioned, new SRD());
            } catch (Exception e) {
                System.out.println(versioned);
                System.out.println(e.getMessage());
            }
        }
        Versioned<Index> versioned = store.get(KEY, new SRD());
        System.out.println("Total entries: " + versioned.getValue().getEntryCount() + " equals: "
                + (versioned.getValue().getEntryCount() == ((N_TIMES * N_THREADS) + 1)));
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[N_THREADS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ConcurrentUpdate(N_TIMES);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        System.out.println("Over");


    }
}
