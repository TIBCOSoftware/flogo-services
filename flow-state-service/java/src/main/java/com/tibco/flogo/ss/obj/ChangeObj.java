package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import java.util.HashMap;
import java.util.Map;

public class ChangeObj
{

    private Integer id;
    private Integer sequence;
    private Change  change;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ChangeObj()
    {
    }

    public ChangeObj(Integer id, Integer sequence, Change change)
    {
        this.id = id;
        this.sequence = sequence;
        this.change = change;
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
     * @return The sequence
     */
    public Integer getSequence()
    {
        return sequence;
    }

    /**
     * @param sequence The sequence
     */
    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    /**
     * @return The stepData
     */
    public Change getChange()
    {
        return change;
    }

    /**
     * @param change The stepData
     */
    public void setChange(Change change)
    {
        this.change = change;
    }

    @Override
    public String toString() {
        return "ChangeObj{" +
                       "id=" + id +
                       ", sequence=" + sequence +
                       ", change=" + change +
                       '}';
    }
}
