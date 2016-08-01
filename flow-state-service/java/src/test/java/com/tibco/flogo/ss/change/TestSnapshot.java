package com.tibco.flogo.ss.change;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.obj.*;
import com.tibco.flogo.ss.service.PropertyClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mregiste on 3/3/2016.
 */
public class TestSnapshot {
    static RedisDataDao redisDataDao = null;

    public void testSnapshotData()
    {
        HashMap<String,Object> snapshotData = null;
        Map<String, String> snapshot = redisDataDao.getSnapshot("5550197c44e9072a6dd9c8e7a0737311:4");
        if(snapshot == null)
        {
            System.out.println("Snapshot is null");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
            snapshotData = mapper.readValue(snapshot.get(SnapshotInfo.SNAPSHOT_DATA), typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TypeReference<ArrayList<WorkQueue>> typeRefWorkQueue = new TypeReference<ArrayList<WorkQueue>>() {};
        List<WorkQueue> workQueue = mapper.convertValue(snapshotData.get(SnapshotData.WORK_QUEUE), typeRefWorkQueue);
        TypeReference<ArrayList<Attribute>> typeRefAttribute = new TypeReference<ArrayList<Attribute>>() {};
        List<Attribute> attributes = mapper.convertValue(snapshotData.get(SnapshotData.ATTRS), typeRefAttribute);
        System.out.println("");
    }


    public void testSnapshot()
    {
        SnapshotData snapshotData = null;
        Map<String, String> snapshot = redisDataDao.getSnapshot("5550197c44e9072a6dd9c8e7a0737311:1");
        if(snapshot == null)
        {
            System.out.println("Snapshot is null");
            return;
        }

        SnapshotInfo snapshotInfo = redisDataDao.getSnapshotInfo("5550197c44e9072a6dd9c8e7a0737311", 4);
        if(snapshotInfo != null)
            System.out.println(snapshotInfo.toJson());


        List<SnapshotInfo> snapshotInfos = redisDataDao.getSnapshotInfo("5550197c44e9072a6dd9c8e7a0737311");
        for (SnapshotInfo info : snapshotInfos) {
            System.out.println(info.toJson());
        }

//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            snapshotData = mapper.readValue(snaphsot.get("snapshotData"), SnapshotData.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(snapshotData == null)
//        {
//            System.out.println("Snapshot data is null");
//            return;
//        }
//        Snapshot snapshot = new Snapshot();
//        snapshot.setSnapshotData(snapshotData);
//        snapshot.setId(Integer.valueOf(snaphsot.get("id")));
//        snapshot.setFlowID(snaphsot.get("flowID"));
//        snapshot.setState(Integer.valueOf(snaphsot.get("state")));
//        snapshot.setStatus(Integer.valueOf(snaphsot.get("status")));
//        System.out.println(snapshot.toJson());
    }

    public static void main(String[] args) {
        redisDataDao = new RedisDataDao();
        PropertyClient propertyClient = new PropertyClient(null, null, null, null, null,
                                                                  "192.168.1.13", 6379, 3000);
        boolean isConnected = redisDataDao.config(propertyClient);
        if (!isConnected) {
            System.out.println("No database connection");
            return;
        }

        TestSnapshot testChange = new TestSnapshot();
        testChange.testSnapshotData();
        testChange.testSnapshot();
    }
}
