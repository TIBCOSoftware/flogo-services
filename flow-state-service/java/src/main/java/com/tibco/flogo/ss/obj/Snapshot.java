package com.tibco.flogo.ss.obj;


import java.util.HashMap;
import java.util.Map;

public class Snapshot {

    private Integer id;
    private String processID;
    private Integer state;
    private Integer status;
    private SnapshotData snapshotData;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The processID
     */
    public String getProcessID() {
        return processID;
    }

    /**
     *
     * @param processID
     * The processID
     */
    public void setProcessID(String processID) {
        this.processID = processID;
    }

    /**
     *
     * @return
     * The state
     */
    public Integer getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The snapshotData
     */
    public SnapshotData getSnapshotData() {
        return snapshotData;
    }

    /**
     *
     * @param snapshotData
     * The snapshotData
     */
    public void setSnapshotData(SnapshotData snapshotData) {
        this.snapshotData = snapshotData;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                       "id=" + id +
                       ", processID='" + processID + '\'' +
                       ", state=" + state +
                       ", status=" + status +
                       ", snapshotData=" + snapshotData +
                       '}';
    }
}