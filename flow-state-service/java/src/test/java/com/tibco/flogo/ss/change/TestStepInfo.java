package com.tibco.flogo.ss.change;

import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.obj.*;
import com.tibco.flogo.ss.service.PropertyClient;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by mregiste on 6/20/2016.
 */
public class TestStepInfo extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger(TestStepInfo.class.getName());

    @Test
    public void testRead()
    {
        List<StepInfo> steps = RedisDataDao.getInstance().getStepInfo("7af07346f8dc4d3a8af9e2228152e984");
        for (StepInfo step : steps)
        {
            System.out.println(step.toJson());
            LOG.debug(step.toString());
        }

        Collections.sort(steps,new StepIdComp());
        for (StepInfo step : steps)
        {
            System.out.println("Step ID: " +step.getId());
            StepData stepData = step.getStepData();
            if(stepData.getWqChanges() != null)
                wqChanges(stepData.getWqChanges());
            if(stepData.getTdChanges() != null)
                tdChanges(stepData.getTdChanges());
            if(stepData.getLdChanges() != null)
                ldChanges(stepData.getLdChanges());
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

    public void wqChanges(List<WqChange> wqChange)
    {
        for (WqChange change : wqChange) {
            System.out.println(change.toJson());
        }
    }

    public void tdChanges(List<TdChange> tdChange)
    {
        for (TdChange change : tdChange) {
            System.out.println(change.toJson());
        }
    }

    public void ldChanges(List<LdChange> ldChange)
    {
        for (LdChange change : ldChange) {
            System.out.println(change.toJson());
        }
    }

    // sort list by ID from largest to smallest
    class StepIdComp implements Comparator<StepInfo> {

        @Override
        public int compare(StepInfo e1, StepInfo e2) {
            if(e1.getId() < e2.getId()){
                return 1;
            } else {
                return -1;
            }
        }
    }
}
