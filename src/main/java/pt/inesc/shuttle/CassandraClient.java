package pt.inesc.shuttle;

/*
 * Author: Dario Nascimento (dario.nascimento@tecnico.ulisboa.pt)
 * 
 * Instituto Superior Tecnico - University of Lisbon - INESC-ID Lisboa
 * Copyright (c) 2014 - All rights reserved
 */

import java.util.Iterator;
import java.util.List;

import org.jboss.logging.Logger;

import pt.inesc.ask.servlet.RootController;
import voldemort.undoTracker.KeyAccess;
import voldemort.utils.ByteArray;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.BaseEncoding;

public class CassandraClient {


    private static final Logger log = Logger.getLogger(CassandraClient.class.getName());

    private static final int CONCURRENCY = 20;
    private static final int MAX_CONNECTIONS = 10;
    private static final String TABLE_NAME = "requests";
    private static final String KEYSPACE = "requestStore";
    private static final String COL_KEYS = "keys";

    private static final String QUERY_KEYS = new String("select " + COL_KEYS + " from " + TABLE_NAME + " where id=");


    private final Cluster cluster;
    private Session session = null;

    public CassandraClient() {
        PoolingOptions pools = new PoolingOptions();
        pools.setMaxSimultaneousRequestsPerConnectionThreshold(HostDistance.LOCAL, CONCURRENCY);
        pools.setCoreConnectionsPerHost(HostDistance.LOCAL, MAX_CONNECTIONS);
        pools.setMaxConnectionsPerHost(HostDistance.LOCAL, MAX_CONNECTIONS);
        pools.setCoreConnectionsPerHost(HostDistance.REMOTE, MAX_CONNECTIONS);
        pools.setMaxConnectionsPerHost(HostDistance.REMOTE, MAX_CONNECTIONS);

        cluster = new Cluster.Builder().addContactPoints(RootController.DATABASE_SERVER)
                                       .withPoolingOptions(pools)
                                       .withSocketOptions(new SocketOptions().setTcpNoDelay(true))
                                       .build();
        try {
            session = cluster.connect(KEYSPACE);
            Metadata metadata = cluster.getMetadata();
            log.info(String.format("Connected to cluster '%s' on %s.", metadata.getClusterName(), metadata.getAllHosts()));
        } catch (NoHostAvailableException e) {
            log.error("No Cassandra server available");
        }
    }






    public void addKeys(ArrayListMultimap<ByteArray, KeyAccess> accessedKeys, long id) {
        if (session == null)
            return;

        StringBuilder sb = new StringBuilder();
        // in cassandra, update inserts if not exists
        sb.append("update ");
        sb.append(TABLE_NAME);
        sb.append(" set ");
        sb.append(COL_KEYS);
        sb.append(" = [");
        Iterator<ByteArray> it = accessedKeys.keySet().iterator();
        while (it.hasNext()) {
            ByteArray key = it.next();
            // key-store:times.store:times,key-store:times.store:times
            sb.append("'");
            sb.append(BaseEncoding.base64().encode(key.get()));
            sb.append("-");
            String keyValue = generateKeyText(accessedKeys.get(key));
            sb.append(keyValue);
            sb.append("'");
            if (it.hasNext())
                sb.append(",");
        }
        sb.append("] where id=");
        sb.append(id);
        sb.append(";");
        session.execute(sb.toString());
    }

    private String generateKeyText(List<KeyAccess> list) {
        StringBuilder sb = null;
        // key-store:times.store:times.
        for (KeyAccess access : list) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(".");
            }
            sb.append(access.store);
            sb.append(":");
            sb.append(access.times);
        }
        return sb.toString();
    }

    public ArrayListMultimap<ByteArray, KeyAccess> getKeys(long id) {
        if (session == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_KEYS);
        sb.append(id);
        sb.append(";");
        ResultSet result = session.execute(sb.toString());
        ArrayListMultimap<ByteArray, KeyAccess> r = ArrayListMultimap.create();
        Row row = result.one();
        if (row == null) {
            log.error("No registry of accessed keys for rid: " + id);
            return r;
        }

        List<String> l = row.getList(COL_KEYS, String.class);
        for (String s : l) {
            // key-store:times.store:times,key-store:times.store:times
            String[] splitted = s.split("-");
            ByteArray key = new ByteArray(BaseEncoding.base64().decode(splitted[0]));
            String[] storeTimes = splitted[1].split("\\.");

            // parse store:times pair
            for (String storeTime : storeTimes) {
                String[] entries = storeTime.split(":");
                Integer times = Integer.parseInt(entries[1]);
                KeyAccess access = new KeyAccess(entries[0], null, times);
                r.put(key, access);
            }
        }
        return r;
    }

    public void close() {
        cluster.close();
    }

    public Session getSession() {
        return session;
    }
}
