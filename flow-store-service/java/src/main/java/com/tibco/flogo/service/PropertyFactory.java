package com.tibco.flogo.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

//import com.trs.util.ServerType;


/**
 * Created by mregiste on 2/9/2016.
 */
public class PropertyFactory
{
    @NotEmpty
    private String storageType;
    private String driverClass;
    private String url;
    private String username;
    private String password;
    private String host;
    private int    port;
    private int    timeOut;

    @JsonProperty
    public String getDriverClass()
    {
        return driverClass;
    }

    @JsonProperty
    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    @JsonProperty
    public String getUrl()
    {
        return url;
    }

    @JsonProperty
    public void setUrl(String url)
    {
        this.url = url;
    }

    @JsonProperty
    public String getUsername()
    {
        return username;
    }

    @JsonProperty
    public void setUsername(String username)
    {
        this.username = username;
    }

    @JsonProperty
    public String getPassword()
    {
        return password;
    }

    @JsonProperty
    public void setPassword(String password)
    {
        this.password = password;
    }

    @JsonProperty
    public String getHost()
    {
        return host;
    }

    @JsonProperty
    public void setHost(String host)
    {
        this.host = host;
    }

    @JsonProperty
    public int getPort()
    {
        return port;
    }

    @JsonProperty
    public void setPort(int port)
    {
        this.port = port;
    }

    @JsonProperty
    public int getTimeOut()
    {
        return timeOut;
    }

    @JsonProperty
    public void setTimeOut(int timeOut)
    {
        this.timeOut = timeOut;
    }

    public String getStorageType()
    {
        return storageType;
    }

    public void setStorageType(String storageType)
    {
        this.storageType = storageType;
    }

    public PropertyClient build()
    {
        PropertyClient client =
                new PropertyClient(getStorageType(), getDriverClass(), getUrl(), getUsername(), getPassword(),
                                   getHost(), getPort(), getTimeOut());
        return client;
    }
}
