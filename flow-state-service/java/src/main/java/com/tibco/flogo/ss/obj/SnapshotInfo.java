package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by mregiste on 2/20/2016.
 */
public class SnapshotInfo
{
    public static final String PROCESS_ID       = "processID";
    public static final String ID       = "id";
    public static final String STATUS   = "status";
    public static final String STATE    = "state";
    public static final String SNAPSHOT_DATA = "snapshotData";
    public static final String DATE     = "date";

    String id;
    String status;
    String state;
    String snapshot;

    public SnapshotInfo()
    {
    }

    public SnapshotInfo(String id, String status, String state, String snapshot)
    {
        this.id = id;
        this.status = status;
        this.state = state;
        this.snapshot = snapshot;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getSnapshot()
    {
        return snapshot;
    }

    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
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
            return null;
        }
    }
}
