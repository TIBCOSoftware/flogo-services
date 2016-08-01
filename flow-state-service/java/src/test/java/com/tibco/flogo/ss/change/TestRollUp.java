package com.tibco.flogo.ss.change;

import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.obj.*;
import com.tibco.flogo.ss.service.PropertyClient;
import com.tibco.flogo.ss.util.RollUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by mregiste on 7/19/2016.
 */
public class TestRollUp {
    private static final Logger LOG = LoggerFactory.getLogger(RollUp.class.getName());
    static RedisDataDao redisDataDao = null;

    public void testRollUp(String flowId, Integer stepNo)
    {
        RollUpObj rollUpObj = RollUp.rollUp(flowId, stepNo);
        System.out.println(rollUpObj.toJson());
        System.out.println("--------------------- snapshot -------------------");
        SnapshotInfo snapshotInfo = redisDataDao.getSnapshotInfo(flowId, stepNo);
        if(snapshotInfo != null)
            System.out.println(snapshotInfo.toJson());

        // high level compare
        rollUpObj.getAttrs();
        if(!snapshotInfo.getFlowId().equals(rollUpObj.getFlowUri()))
            LOG.error("Flow Id doesn't match");
        if(!snapshotInfo.getId().equals(rollUpObj.getId()))
            LOG.error("Id doesn't match");
        if(!snapshotInfo.getStatus().equals(rollUpObj.getStatus()))
            LOG.error("Status doesn't match");
        if(!snapshotInfo.getState().equals(rollUpObj.getState()))
            LOG.error("State doesn't match");

        rollUpObj.getAttrs();
        SnapshotData ruSnapshotData = rollUpObj.getSnapshot();
        SnapshotData snapshotData = snapshotInfo.getSnapshot();
        if(ruSnapshotData.getAttrs().size() != snapshotData.getAttrs().size())
            LOG.error("Attribute count doesn't match");
        if(ruSnapshotData.getWorkQueue().size() != snapshotData.getWorkQueue().size())
            LOG.error("Work Queue count doesn't match");

        RootTaskEnv ruRootTaskEnv = ruSnapshotData.getRootTaskEnv();
        RootTaskEnv rootTaskEnv = snapshotData.getRootTaskEnv();
        List<TaskData> ruTaskDataList = ruRootTaskEnv.getTaskDatas();
        List<TaskData> taskDataList = rootTaskEnv.getTaskDatas();
        if(ruTaskDataList.size() != taskDataList.size())
            LOG.error("Task count doesn't match");

        List<LinkData> ruLinkDataList = ruRootTaskEnv.getLinkDatas();
        List<LinkData> linkDataList = rootTaskEnv.getLinkDatas();
        if(ruLinkDataList.size() != linkDataList.size())
            LOG.error("Link count doesn't match");
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

        TestRollUp testRollUp = new TestRollUp();
        testRollUp.testRollUp("0414483e99633add49607bf2dbc63c01", 4);
//        testRollUp.testRollUp("7af07346f8dc4d3a8af9e2228152e984", 5);
//        testRollUp.testRollUp("7af07346f8dc4d3a8af9e2228152e984", 4);
//        testRollUp.testRollUp("7af07346f8dc4d3a8af9e2228152e984", 3);
//        testRollUp.testRollUp("7af07346f8dc4d3a8af9e2228152e984", 2);
    }
}
