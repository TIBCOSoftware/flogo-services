package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import java.util.Map;


public class WqChange
{
    private Integer  ChgType;
    private Integer  Id;
    private WorkItem WorkItem;

    public WqChange()
    {
    }

    public WqChange(Integer chgType, Integer id, WorkItem workItem)
    {
        ChgType = chgType;
        Id = id;
        WorkItem = workItem;
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
     * @return The WorkItem
     */
    public WorkItem getWorkItem()
    {
        return WorkItem;
    }

    /**
     * @param WorkItem The WorkItem
     */
    public void setWorkItem(WorkItem WorkItem)
    {
        this.WorkItem = WorkItem;
    }
}


