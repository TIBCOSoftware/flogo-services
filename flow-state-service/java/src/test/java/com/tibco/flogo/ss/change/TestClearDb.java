package com.tibco.flogo.ss.change;

import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.service.PropertyClient;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mregiste on 7/7/2016.
 */
public class TestClearDb extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger(TestClearDb.class.getName());

    @Test
    public void testDelete()
    {
        try
        {
            String ret = RedisDataDao.getInstance().drop();
            System.out.println("Flushed DB: " +ret);
        }
        catch (Exception ex)
        {
            LOG.error("Delete DB: " + ex.getMessage());
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
