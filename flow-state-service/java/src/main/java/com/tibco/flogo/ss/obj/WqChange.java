package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class WqChange
{
    private static final Logger LOG = LoggerFactory.getLogger(WqChange.class.getName());

    @JsonProperty("ChgType")
    private Integer  chgType;
    @JsonProperty("ID")
    private Integer  id;
    @JsonProperty("WorkItem")
    private WorkItem workItem;

    public WqChange()
    {
    }

    public WqChange(Integer chgType, Integer id, WorkItem workItem)
    {
        this.chgType = chgType;
        this.id = id;
        this.workItem = workItem;
    }

    /**
     * @return The ChgType
     */
    public Integer getChgType()
    {
        return chgType;
    }

    /**
     * @param chgType The ChgType
     */
    public void setChgType(Integer chgType)
    {
        this.chgType = chgType;
    }

    /**
     * @return The Id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id The Id
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return The WorkItem
     */
    public WorkItem getWorkItem()
    {
        return workItem;
    }

    /**
     * @param workItem The WorkItem
     */
    public void setWorkItem(WorkItem workItem)
    {
        this.workItem = workItem;
    }

    @Override
    public String toString() {
        return "WqChange{" +
                "chgType=" + chgType +
                ", id=" + id +
                ", workItem=" + workItem +
                '}';
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString(this);
        }
        catch (IOException e)
        {
            LOG.error("Account JSON conversion error");
            return null;
        }
    }
}


