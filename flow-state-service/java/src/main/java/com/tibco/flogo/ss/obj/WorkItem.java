package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class WorkItem
{
    private static final Logger LOG = LoggerFactory.getLogger(WorkItem.class.getName());

    private Integer id;
    private Integer execType;
    private Integer code;
    private Integer taskID;

    public WorkItem()
    {
    }

    public WorkItem(Integer id, Integer execType, Integer code)
    {
        this.id = id;
        this.execType = execType;
        this.code = code;
    }

    /**
     * @return The id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return The execType
     */
    public Integer getExecType()
    {
        return execType;
    }

    /**
     * @param execType The execType
     */
    public void setExecType(Integer execType)
    {
        this.execType = execType;
    }

    /**
     * @return The code
     */
    public Integer getCode()
    {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(Integer code)
    {
        this.code = code;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    @Override
    public String toString() {
        return "WorkItem{" +
                "id=" + id +
                ", execType=" + execType +
                ", code=" + code +
                ", taskID=" + taskID +
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