package pt.inesc.shuttle;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.RUD;
import voldemort.utils.ByteArray;

import com.google.common.collect.ArrayListMultimap;

public class ShuttleInterceptor
        implements HandlerInterceptor {
    private static final Logger log = Logger.getLogger(ShuttleInterceptor.class.getName());
    CassandraClient cassandra = new CassandraClient();

    public ShuttleInterceptor() {
        super();
    }


    static {
        System.setProperty("appengine.disableRestrictedCheck", "true");
    }

    // static ConcurrentHashMap<Long, RUD> mapRequestRud = new ConcurrentHashMap<Long,
    // RUD>();
    VoldemortUnlocker databaseUnlocker = new VoldemortUnlocker();



    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception exception) throws Exception {
        RUD rud = (RUD) req.getAttribute("rud");
        // mapRequestRud.remove(Thread.currentThread().getId(),rud);

        // Unlocking algorithm
        ArrayListMultimap<ByteArray, KeyAccess> accessedKeys = rud.getAccessedKeys();

        // If is redo:
        if (rud.redo) {
            // get the keys used before
            ArrayListMultimap<ByteArray, KeyAccess> originalKeys = cassandra.getKeys(rud.rid);
            log.debug("originalKeys keys" + originalKeys);
            log.debug("accessed keys" + accessedKeys);


            subtrackTables(accessedKeys, originalKeys);


            if (!accessedKeys.isEmpty()) {
                log.debug("Unlock keys:" + accessedKeys);
                databaseUnlocker.unlockKeys(accessedKeys, rud);
            }
        } else {
            if (!accessedKeys.isEmpty())
                log.debug("Store keys: " + accessedKeys);
            cassandra.addKeys(accessedKeys, rud.rid);
        }
    }

    public void subtrackTables(ArrayListMultimap<ByteArray, KeyAccess> newTable, ArrayListMultimap<ByteArray, KeyAccess> originalTable) {
        Iterable<ByteArray> originalKeys = originalTable.keySet();
        for (ByteArray key : originalKeys) {
            List<KeyAccess> newList = newTable.get(key);
            List<KeyAccess> originalList = originalTable.get(key);
            subtrackList(newList, originalList);
        }
    }


    private void subtrackList(List<KeyAccess> newList, List<KeyAccess> originalList) {
        // subtract the original from the originalKeys
        for (KeyAccess oAccess : originalList) {
            int index = newList.indexOf(oAccess);
            if (index == -1) {
                newList.add(oAccess);
            } else {
                KeyAccess newAccess = newList.get(index);
                newAccess.times = oAccess.times - newAccess.times;
                if (newAccess.times <= 0) {
                    newList.remove(index);
                }
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        RUD rud;
        try {
            long rid = Long.parseLong(req.getHeader("Id"));
            short branch = Short.parseShort(req.getHeader("B"));
            boolean restrain = (req.getHeader("R").equals("t"));
            boolean redo = (req.getHeader("Redo").equals("t"));
            rud = new RUD(rid, branch, restrain, redo);
        } catch (NumberFormatException e) {
            // No rud from proxy, create stub using local clock
            // rud = new RUD(0L);
            rud = new RUD(System.currentTimeMillis());
        }

        // mapRequestRud.put(Thread.currentThread().getId(),rud);
        req.setAttribute("rud", rud);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

    }


}
