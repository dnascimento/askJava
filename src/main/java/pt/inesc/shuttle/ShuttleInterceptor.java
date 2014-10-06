package pt.inesc.shuttle;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.SRD;
import voldemort.utils.ByteArray;

import com.google.common.collect.ArrayListMultimap;

public class ShuttleInterceptor
        implements HandlerInterceptor {
    private static final Logger log = Logger.getLogger(ShuttleInterceptor.class.getName());
    static CassandraClient cassandra = new CassandraClient();

    public ShuttleInterceptor() {
        super();
    }


    static {
        System.setProperty("appengine.disableRestrictedCheck", "true");
    }

    // static ConcurrentHashMap<Long, SRD> mapRequestRud = new ConcurrentHashMap<Long,
    // SRD>();
    VoldemortUnlocker databaseUnlocker = new VoldemortUnlocker();



    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception exception) throws Exception {
        SRD srd = (SRD) req.getAttribute("srd");
        // mapRequestRud.remove(Thread.currentThread().getId(),srd);

        // Unlocking algorithm
        ArrayListMultimap<ByteArray, KeyAccess> accessedKeys = srd.getAccessedKeys();

        // If is redo:
        if (srd.redo) {
            // get the keys used before
            ArrayListMultimap<ByteArray, KeyAccess> originalKeys = cassandra.getKeys(srd.rid);
            // LOG.debug("originalKeys keys" + originalKeys);
            // LOG.debug("accessed keys" + accessedKeys);


            subtrackTables(accessedKeys, originalKeys);


            if (!accessedKeys.isEmpty()) {
                // LOG.debug("Unlock keys:" + accessedKeys);
                databaseUnlocker.unlockKeys(accessedKeys, srd);
            }
        } else {
            if (!accessedKeys.isEmpty())
                // LOG.debug("Store keys: " + accessedKeys);
                cassandra.addKeys(accessedKeys, srd.rid);
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
        SRD srd;
        String id = req.getHeader("Id");
        if (id != null) {
            try {
                long rid = Long.valueOf(id);
                short branch = Short.valueOf(req.getHeader("B"));
                boolean restrain = (req.getHeader("R").equals("t"));
                boolean redo = (req.getHeader("Redo").equals("t"));
                srd = new SRD(rid, branch, restrain, redo);
            } catch (NumberFormatException e) {
                // No srd from proxy, create stub using local clock
                srd = new SRD(System.currentTimeMillis());
            }
        } else {
            srd = new SRD(System.currentTimeMillis());
        }

        // mapRequestRud.put(Thread.currentThread().getId(),srd);
        req.setAttribute("srd", srd);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {

    }


}
