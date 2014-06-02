package pt.inesc.shuttle;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.RUD;

public class ShuttleInterceptor
        implements HandlerInterceptor {
    private static final Logger log = LogManager.getLogger(ShuttleInterceptor.class.getName());

    public ShuttleInterceptor() {
        super();
    }

    private static final boolean IS_UNSAFE_SUPPORTED;

    static {

        boolean isUnsafeFound;

        try {
            isUnsafeFound = Class.forName("sun.misc.Unsafe") != null;
        } catch (Throwable t) {
            isUnsafeFound = false;
        }

        IS_UNSAFE_SUPPORTED = isUnsafeFound;
    }

    // static ConcurrentHashMap<Long, RUD> mapRequestRud = new ConcurrentHashMap<Long,
    // RUD>();
    CassandraClient cassandra = new CassandraClient();
    VoldemortUnlocker databaseUnlocker = new VoldemortUnlocker();

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
            log.error("originalKeys keys" + originalKeys);

            // subtract the accessed keys from original keys
            originalKeys.removeAll(rud.getAccessedKeys());
            log.error("accessed keys" + rud.getAccessedKeys());

            if (!originalKeys.isEmpty()) {
                log.error("Unlock keys: " + originalKeys);
                databaseUnlocker.unlockKeys(originalKeys, rud);
            }
        } else {
            if (!accessedKeys.isEmpty())
                log.error("Store keys: " + accessedKeys);
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


}
