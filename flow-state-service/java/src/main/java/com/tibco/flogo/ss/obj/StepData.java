package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepData
{
    private Integer status;
    private Integer state;
    private List<WqChange> wqChanges = new ArrayList<>();
    private List<TdChange> tdChanges = new ArrayList<>();
    private List<LdChange> ldChanges = new ArrayList<>();
    private List<Attr> attrs = new ArrayList<>();

    public Integer getStatus()
    {
        return this.status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getState()
    {
        return this.state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public List<WqChange> getWqChanges()
    {
        return this.wqChanges;
    }

    public void setWqChanges(List<WqChange> wqChanges)
    {
        this.wqChanges = wqChanges;
    }

    public List<TdChange> getTdChanges()
    {
        return this.tdChanges;
    }

    public void setTdChanges(List<TdChange> tdChanges)
    {
        this.tdChanges = tdChanges;
    }

    public List<LdChange> getLdChanges() {
        return ldChanges;
    }

    public void setLdChanges(List<LdChange> ldChanges) {
        this.ldChanges = ldChanges;
    }

    public List<Attr> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attr> attrs) {
        this.attrs = attrs;
    }

    public String toString()
    {
        return "StepData{status=" + this.status + ", state=" + this.state + ", wqChanges=" + this.wqChanges + ", tdChanges=" + this.tdChanges + ", ldChanges=" + this.ldChanges + '}';
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch (IOException e)
        {
            return null;
        }
    }
}