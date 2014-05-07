package pt.inesc.shuttle;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import voldemort.undoTracker.KeyAccess;
import voldemort.undoTracker.RUD;

public class ShuttleInterceptor
        implements HandlerInterceptor {

    CassandraClient cassandra = new CassandraClient();

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception exception) throws Exception {
        // Called after view rendering
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) throws Exception {
        RUD rud = (RUD) req.getAttribute("rud");

        // Unlocking algorithm
        Set<KeyAccess> accessedKeys = rud.getAccessedKeys();

        // If is redo:
        if (rud.redo) {
            // get the keys used before
            Set<KeyAccess> originalKeys = cassandra.getKeys(rud.rid);
            // subtract the accessed keys from original keys
            originalKeys.removeAll(rud.getAccessedKeys());

            System.out.println("Remain keys: " + originalKeys);
            // TODO send message to unlock the keys
        } else {
            if (!accessedKeys.isEmpty())
                cassandra.addKeys(accessedKeys, rud.rid);
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
            rud = new RUD(System.currentTimeMillis());
        }
        req.setAttribute("rud", rud);
        return true;
    }

    private void unlockKeys(Set<KeyAccess> accessedKeys) {
        // TODO sort by store
        // TODO connect to other stores
    }
}
