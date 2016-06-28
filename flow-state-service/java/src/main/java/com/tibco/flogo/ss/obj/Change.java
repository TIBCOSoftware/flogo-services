package com.tibco.flogo.ss.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mregiste on 2/21/2016.
 */
public class Change
{
    private Integer status;
    private Integer state;
    private List<WqChange>      wqChanges            = new ArrayList<WqChange>();
    private List<TdChange>      tdChanges            = new ArrayList<TdChange>();

    /**
     * @return The status
     */
    public Integer getStatus()
    {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(Integer status)
    {
        this.status = status;
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
     * @return The wqChanges
     */
    public List<WqChange> getWqChanges()
    {
        return wqChanges;
    }

    /**
     * @param wqChanges The wqChanges
     */
    public void setWqChanges(List<WqChange> wqChanges)
    {
        this.wqChanges = wqChanges;
    }

    /**
     * @return The tdChanges
     */
    public List<TdChange> getTdChanges()
    {
        return tdChanges;
    }

    /**
     * @param tdChanges The tdChanges
     */
    public void setTdChanges(List<TdChange> tdChanges)
    {
        this.tdChanges = tdChanges;
    }

    @Override
    public String toString() {
        return "Change{" +
                       "status=" + status +
                       ", state=" + state +
                       ", wqChanges=" + wqChanges +
                       ", tdChanges=" + tdChanges +
                       '}';
    }
}
