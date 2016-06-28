package com.tibco.flogo.dao.impl;

import com.tibco.flogo.dao.DataDao;
import com.tibco.flogo.service.PropertyClient;
import com.tibco.flogo.util.StorageType;
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
