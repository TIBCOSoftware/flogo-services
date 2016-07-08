package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import java.util.Map;

public class WorkItem
{
    private Integer id;
    private Integer execType;
    private Integer code;

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
}