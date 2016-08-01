package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by mregiste on 2/20/2016.
 */
public class SnapshotInfo {
    public static final String FLOW_ID = "flowID";
    public static final String ID = "id";
    public static final String STATUS = "status";
    public static final String STATE = "state";
    public static final String SNAPSHOT_DATA = "snapshotData";
    public static final String DATE = "date";

    private Integer id = 0;
    @JsonProperty("flowID")
    private String flowId;
    private Integer status;
    private Integer state;
    private SnapshotData snapshot;
    private String date;
    @JsonIgnore
    public SnapshotData snapshotData;

    public SnapshotInfo() {
    }

    public SnapshotInfo(String flowId, Integer id, Integer status, Integer state, SnapshotData snapshot) {
        this.flowId = flowId;
        this.id = id;
        this.status = status;
        this.state = state;
        this.snapshot = snapshot;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public SnapshotData getSnapshot() {
        return snapshot;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public void setSnapshot(SnapshotData snapshot) {
        this.snapshot = snapshot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }
}
