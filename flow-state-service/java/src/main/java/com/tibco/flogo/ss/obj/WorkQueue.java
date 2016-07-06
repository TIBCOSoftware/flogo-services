package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mregiste on 3/3/2016.
 */
public class WorkQueue {
    private static final Logger LOG = LoggerFactory.getLogger(WorkQueue.class.getName());

    private Integer id;
    private Integer execType;
    private Integer code;
    private Integer taskID;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The execType
     */
    public Integer getExecType() {
        return execType;
    }

    /**
     *
     * @param execType
     * The execType
     */
    public void setExecType(Integer execType) {
        this.execType = execType;
    }

    /**
     *
     * @return
     * The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     *
     * @param code
     * The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     *
     * @return
     * The taskID
     */
    public Integer getTaskID() {
        return taskID;
    }

    /**
     *
     * @param taskID
     * The taskID
     */
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    @Override
    public String toString() {
        return "WorkQueue{" +
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
