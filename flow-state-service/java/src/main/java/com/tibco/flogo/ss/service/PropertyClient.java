package com.tibco.flogo.ss.service;

import com.tibco.flogo.ss.util.StorageType;


/**
 * Created by mregiste on 2/9/2016.
 */
public class PropertyClient
{
    private String storageType;
    private String driverClass;
    private String url;
    private String username;
    private String password;
    private String host;
    private int    port;
    private int    timeOut;

    public PropertyClient(String storageType, String driverClass, String url, String username, String password,
                          String host, int port, int timeOut)
    {
        this.storageType = storageType;
        this.driverClass = driverClass;
        this.url = url;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.timeOut = timeOut;
    }

    public PropertyClient(String storageType, String driverClass, String url, String username, String password)
    {
        this.storageType = storageType;
        this.driverClass = driverClass;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public StorageType getStorageTypeEnum()
    {
        return StorageType.valueOf(storageType);
    }

    public String getStorageType()
    {
        return storageType;
    }

    public void setStorageType(String storageType)
    {
        this.storageType = storageType;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getTimeOut()
    {
        return timeOut;
    }

    public void setTimeOut(int timeOut)
    {
        this.timeOut = timeOut;
    }
}
