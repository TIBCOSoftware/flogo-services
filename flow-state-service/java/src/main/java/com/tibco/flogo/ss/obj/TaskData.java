package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import java.util.HashMap;
import java.util.Map;

public class TaskData
{

    private Integer state;
    private Boolean done;
    private Object  attrs;
    private Integer taskId;

    public TaskData()
    {
    }

    public TaskData(Integer state, Boolean done, Object attrs, Integer taskId)
    {
        this.state = state;
        this.done = done;
        this.attrs = attrs;
        this.taskId = taskId;
    }

    /**
     * @return The status
     */
    public Integer getState()
    {
        return state;
    }

    /**
     * @param state The status
     */
    public void setState(Integer state)
    {
        this.state = state;
    }

    /**
     * @return The done
     */
    public Boolean getDone()
    {
        return done;
    }

    /**
     * @param done The done
     */
    public void setDone(Boolean done)
    {
        this.done = done;
    }

    /**
     * @return The attrs
     */
    public Object getAttrs()
    {
        return attrs;
    }

    /**
     * @param attrs The attrs
     */
    public void setAttrs(Object attrs)
    {
        this.attrs = attrs;
    }

    /**
     * @return The taskId
     */
    public Integer getTaskId()
    {
        return taskId;
    }

    /**
     * @param taskId The taskId
     */
    public void setTaskId(Integer taskId)
    {
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskData taskData = (TaskData) o;

        return taskId.equals(taskData.taskId);

    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }

    @Override
    public String toString() {
        return "TaskData{" +
                       "state=" + state +
                       ", done=" + done +
                       ", attrs=" + attrs +
                       ", taskId=" + taskId +
                       '}';
    }
}
