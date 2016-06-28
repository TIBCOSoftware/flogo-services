package com.tibco.flogo.ss.obj;


import java.util.HashMap;
import java.util.Map;

public class Snapshot {

    private Integer id;
    private String flowID;
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
     * The flowID
     */
    public String getFlowID() {
        return flowID;
    }

    /**
     *
     * @param flowID
     * The flowID
     */
    public void setFlowID(String flowID) {
        this.flowID = flowID;
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
                       ", flowID='" + flowID + '\'' +
                       ", state=" + state +
                       ", status=" + status +
                       ", snapshotData=" + snapshotData +
                       '}';
    }
}