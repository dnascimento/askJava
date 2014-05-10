package pt.inesc.shuttle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import pt.inesc.shuttle.unlock.DatabaseClientCache;
import voldemort.client.StoreClient;
import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.RUD;
import voldemort.utils.ByteArray;

public class ShuttleInterceptor
        implements HandlerInterceptor {

    // static ConcurrentHashMap<Long, RUD> mapRequestRud = new ConcurrentHashMap<Long,
    // RUD>();
    CassandraClient cassandra = new CassandraClient();
    DatabaseClientCache databaseClients = new DatabaseClientCache();

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception exception) throws Exception {
        // Called after view rendering
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) throws Exception {
        RUD rud = (RUD) req.getAttribute("rud");
        // mapRequestRud.remove(Thread.currentThread().getId(),rud);

        // Unlocking algorithm
        Set<KeyAccess> accessedKeys = rud.getAccessedKeys();

        // If is redo:
        if (rud.redo) {
            // get the keys used before
            Set<KeyAccess> originalKeys = cassandra.getKeys(rud.rid);
            // subtract the accessed keys from original keys
            originalKeys.removeAll(rud.getAccessedKeys());

            System.out.println("Remain keys: " + originalKeys);
            if (!originalKeys.isEmpty())
                unlockKeys(originalKeys, rud);
        } else {
            if (!accessedKeys.isEmpty())
                cassandra.addKeys(accessedKeys, rud.rid);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        RUD rud;
        // System.out.println("Pre thread: " + Thread.currentThread().getId());
        try {
            long rid = Long.parseLong(req.getHeader("Id"));
            short branch = Short.parseShort(req.getHeader("B"));
            boolean restrain = (req.getHeader("R").equals("t"));
            boolean redo = (req.getHeader("Redo").equals("t"));
            rud = new RUD(rid, branch, restrain, redo);
        } catch (NumberFormatException e) {
            // No rud from proxy, create stub using local clock
            rud = new RUD(System.currentTimeMillis());
        }

        // mapRequestRud.put(Thread.currentThread().getId(),rud);
        req.setAttribute("rud", rud);
        return true;
    }

    /**
     * @param accessedKeys Not empty set of keys to unlock
     */
    private void unlockKeys(Set<KeyAccess> unlockKeys, RUD rud) {
        ArrayList<KeyAccess> list = new ArrayList<KeyAccess>(unlockKeys);
        // sort by store
        Collections.sort(list);
        String lastStore = list.get(0).store;
        StoreClient<ByteArray, Object> client;
        ArrayList<ByteArray> keys = new ArrayList<ByteArray>();

        for (int i = 0; i < list.size(); i++) {
            KeyAccess k = list.get(i);
            if (k.store.equals(lastStore)) {
                keys.add(k.key);
            } else {
                client = databaseClients.get(lastStore);
                client.unlockKeys(keys, rud);
                keys.clear();
                lastStore = k.store;
                keys.add(k.key);
            }
        }
    }
}
