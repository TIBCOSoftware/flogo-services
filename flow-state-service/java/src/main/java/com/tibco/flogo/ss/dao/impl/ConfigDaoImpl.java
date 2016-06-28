package com.tibco.flogo.ss.dao.impl;

import com.tibco.flogo.ss.dao.DataDao;
import com.tibco.flogo.ss.service.PropertyClient;
import com.tibco.flogo.ss.util.StorageType;
import org.apache.log4j.Logger;


/**
 * Created by mregiste on 2/10/2016.
 */
public class ConfigDaoImpl
{
    private static final Logger LOG = Logger.getLogger(ConfigDaoImpl.class);
    private static DataDao theInstance;

    public static boolean init(PropertyClient propertyClient)
    {
        final StorageType serverType = propertyClient.getStorageTypeEnum();
        boolean isConnected;
        LOG.info("Server Type: " + serverType);
        switch (serverType)
        {
            case REDIS:
                RedisDataDao redisDataDao = new RedisDataDao();
                isConnected = redisDataDao.config(propertyClient);
                if (!isConnected)
                    return false;
                theInstance = redisDataDao;
                break;
            case INMEM:
                break;
        }
        return true;
    }

    public static DataDao getInstance()
    {
        return theInstance;
    }
}
