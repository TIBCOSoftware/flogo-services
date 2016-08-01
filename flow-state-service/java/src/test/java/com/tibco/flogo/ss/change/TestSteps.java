package com.tibco.flogo.ss.change;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.handlers.CustomException;
import com.tibco.flogo.ss.obj.StepData;
import com.tibco.flogo.ss.service.PropertyClient;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mregiste on 7/7/2016.
 */
public class TestSteps extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger(TestSteps.class.getName());

    @Test
    public void testRead()
    {
        Set<String> steps = RedisDataDao.getInstance().listAllFlowStepIds();
        for (String step : steps) {
            System.out.println("Step: " +step);
        }
    }

    @Test
    public void testListSteps()
    {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> stepData = RedisDataDao.getInstance().listFlowStepData("7af07346f8dc4d3a8af9e2228152e984");
        for (Map<String, String> stringStringMap : stepData) {
            for (Map.Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
                System.out.println("Entry: " +stringStringEntry);
            }

            StepData stepDataObj = mapper.convertValue(stepData, StepData.class);
            System.out.println("Step: " +stepDataObj.toJson());
        }
    }

    @Test
    public void testSteps()
    {
        Set<String> stepFlows = RedisDataDao.getInstance().listAllFlowStepIds();
        for (String stepFlow : stepFlows) {
            System.out.println("Step Flow: " +stepFlow);

            Map<String, String> step = RedisDataDao.getInstance().listStepData(stepFlow);
            for (Map.Entry<String, String> stringStringEntry : step.entrySet()) {
                System.out.println("Entry: " +stringStringEntry);
            }

//            ObjectMapper mapper = new ObjectMapper();
//            StepData stepData = mapper.convertValue(step.get("stepData"), StepData.class);
//            System.out.println("Step: " +stepData.toJson());
        }
    }

//    @Test
//    public void testCleanupFlow()
//    {
//        Set<String> flows = RedisDataDao.getInstance().listFlows();
//        for (String flow : flows) {
//            System.out.println("flow: " +flow);
//
//            String flowInstanceId = flow.replaceAll("^instance:", "");
//            System.out.println("Status: " +RedisDataDao.getInstance().getInstanceStatus(flowInstanceId));
//            System.out.println("flow id: " +flowInstanceId);
//            System.out.println("snapshot status: " +RedisDataDao.getInstance().getSnapshotStatus(flowInstanceId));
////            deleteFlow(flowInstanceId);
//        }
//    }

    public void deleteFlow(String flowId)
    {
        long ret = -1;
        try
        {
            ret = RedisDataDao.getInstance().removeSnapshot(flowId);
            if (ret == 0)
                LOG.error("Snapshot: " + flowId + " not found or an error occurred");
            System.out.println("removeSnapshot: " +ret);
            ret = RedisDataDao.getInstance().removeSteps(flowId);
            if (ret == 0)
                LOG.error("Step: " + flowId + " not found or an error occurred");
            System.out.println("removeStep: " +ret);
            ret = RedisDataDao.getInstance().removeFlow(flowId);
            if (ret == 0)
                LOG.error("Flow: " + flowId + " not found or an error occurred");
        }
        catch (Exception ex)
        {
            LOG.error("Delete for snapshot: " + flowId + " failed: "
                    + ex.getMessage());
        }


    }

    @Before
    public void setUp() {
        PropertyClient propertyClient = new PropertyClient(null, null, null, null, null,
                "192.168.1.13", 6379, 3000);
        boolean isConnected = RedisDataDao.getInstance().config(propertyClient);
        if (!isConnected) {
            System.out.println("No database connection");
        }
    }
}
