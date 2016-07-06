package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TdChange
{
    private static final Logger LOG = LoggerFactory.getLogger(TdChange.class.getName());

    @JsonProperty("ChgType")
    private Integer  chgType;
    @JsonProperty("ID")
    private Integer  id;
    @JsonProperty("TaskData")
    private TaskData taskData;

    public TdChange()
    {
    }

    public TdChange(Integer chgType, Integer id, TaskData taskData)
    {
        this.chgType = chgType;
        this.id = id;
        this.taskData = taskData;
    }

    /**
     * @return The ChgType
     */
    public Integer getChgType()
    {
        return chgType;
    }

    /**
     * @param ChgType The ChgType
     */
    public void setChgType(Integer ChgType)
    {
        this.chgType = ChgType;
    }

    /**
     * @return The Id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param Id The Id
     */
    public void setId(Integer Id)
    {
        this.id = Id;
    }

    /**
     * @return The TaskData
     */
    public TaskData getTaskData()
    {
        return taskData;
    }

    /**
     * @param taskData The TaskData
     */
    public void setTaskData(TaskData taskData)
    {
        this.taskData = taskData;
    }

    @Override
    public String toString() {
        return "TdChange{" +
                       "ChgType=" + chgType +
                       ", Id=" + id +
                       ", TaskData=" + taskData +
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
