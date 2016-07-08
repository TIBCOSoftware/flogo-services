package com.tibco.flogo.ss.change;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.obj.Snapshot;
import com.tibco.flogo.ss.obj.SnapshotData;
import com.tibco.flogo.ss.service.PropertyClient;

import java.io.IOException;
import java.util.Map;


/**
 * Created by mregiste on 3/3/2016.
 */
public class TestSnapshot {
    static RedisDataDao redisDataDao = null;

    public void testSnapshot()
    {
        SnapshotData snapshotData = null;
        Map<String, String> snaphsot = redisDataDao.getSnapshot("e251992c69d0bf5e760beba211f2af1c", 1);
        if(snaphsot == null)
        {
            System.out.println("Snapshot is null");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            snapshotData = mapper.readValue(snaphsot.get("snapshotData"), SnapshotData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(snapshotData == null)
        {
            System.out.println("Snapshot data is null");
            return;
        }
        Snapshot snapshot = new Snapshot();
        snapshot.setSnapshotData(snapshotData);
        snapshot.setId(Integer.valueOf(snaphsot.get("id")));
        snapshot.setFlowID(snaphsot.get("flowID"));
        snapshot.setState(Integer.valueOf(snaphsot.get("state")));
        snapshot.setStatus(Integer.valueOf(snaphsot.get("status")));
        System.out.println(snapshot);
    }

    public static void main(String[] args) {
        redisDataDao = new RedisDataDao();
        PropertyClient propertyClient = new PropertyClient(null, null, null, null, null,
                                                                  "192.168.99.101", 6379, 3000);
        boolean isConnected = redisDataDao.config(propertyClient);
        if (!isConnected) {
            System.out.println("No database connection");
            return;
        }

        TestSnapshot testChange = new TestSnapshot();
        testChange.testSnapshot();
    }
}
