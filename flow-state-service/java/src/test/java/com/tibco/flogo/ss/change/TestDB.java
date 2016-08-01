package com.tibco.flogo.ss.change;

import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.service.PropertyClient;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mregiste on 7/11/2016.
 */
public class TestDB extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger(TestSteps.class.getName());

    @Test
    public void testDelPattern()
    {

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
