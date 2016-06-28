package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import java.util.Map;

public class TdChange
{

    private Integer  ChgType;
    private Integer  Id;
    private TaskData TaskData;

    public TdChange()
    {
    }

    public TdChange(Integer chgType, Integer id, com.tibco.flogo.ss.obj.TaskData taskData)
    {
        ChgType = chgType;
        Id = id;
        TaskData = taskData;
    }

    /**
     * @return The ChgType
     */
    public Integer getChgType()
    {
        return ChgType;
    }

    /**
     * @param ChgType The ChgType
     */
    public void setChgType(Integer ChgType)
    {
        this.ChgType = ChgType;
    }

    /**
     * @return The Id
     */
    public Integer getId()
    {
        return Id;
    }

    /**
     * @param Id The Id
     */
    public void setId(Integer Id)
    {
        this.Id = Id;
    }

    /**
     * @return The TaskData
     */
    public TaskData getTaskData()
    {
        return TaskData;
    }

    /**
     * @param TaskData The TaskData
     */
    public void setTaskData(TaskData TaskData)
    {
        this.TaskData = TaskData;
    }

    @Override
    public String toString() {
        return "TdChange{" +
                       "ChgType=" + ChgType +
                       ", Id=" + Id +
                       ", TaskData=" + TaskData +
                       '}';
    }
}
