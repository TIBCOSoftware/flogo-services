package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskData
{
    private static final Logger LOG = LoggerFactory.getLogger(TaskData.class.getName());

    private String state;
    private List<Attribute> attrs = new ArrayList<Attribute>();
    private String taskId;

    public TaskData() {
    }

    public TaskData(String state, List<Attribute> attrs, String taskId) {
        this.state = state;
        this.attrs = attrs;
        this.taskId = taskId;
    }

    public static Logger getLOG() {
        return LOG;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Attribute> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attribute> attrs) {
        this.attrs = attrs;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "TaskData{" +
                "state='" + state + '\'' +
                ", attrs=" + attrs +
                ", taskId='" + taskId + '\'' +
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
