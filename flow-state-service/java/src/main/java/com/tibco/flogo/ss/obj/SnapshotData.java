package com.tibco.flogo.ss.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mregiste on 3/3/2016.
 */
public class SnapshotData {
    private String id;
    private Integer status;
    private Integer state;
    private List<Attribute> attrs = new ArrayList<Attribute>();
    private String flowURI;
    private List<WorkQueue> workQueue = new ArrayList<WorkQueue>();
    private RootTask rootTask;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
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
     * The attrs
     */
    public List<Attribute> getAttrs() {
        return attrs;
    }

    /**
     *
     * @param attrs
     * The attrs
     */
    public void setAttrs(List<Attribute> attrs) {
        this.attrs = attrs;
    }

    /**
     *
     * @return
     * The flowURI
     */
    public String getFlowURI() {
        return flowURI;
    }

    /**
     *
     * @param flowURI
     * The flowURI
     */
    public void setFlowURI(String flowURI) {
        this.flowURI = flowURI;
    }

    /**
     *
     * @return
     * The workQueue
     */
    public List<WorkQueue> getWorkQueue() {
        return workQueue;
    }

    /**
     *
     * @param workQueue
     * The workQueue
     */
    public void setWorkQueue(List<WorkQueue> workQueue) {
        this.workQueue = workQueue;
    }

    /**
     *
     * @return
     * The rootTaskEnv
     */
    public RootTask getRootTaskEnv() {
        return rootTask;
    }

    /**
     *
     * @param rootTask
     * The rootTaskEnv
     */
    public void setRootTaskEnv(RootTask rootTask) {
        this.rootTask = rootTask;
    }

    @Override
    public String toString() {
        return "SnapshotData{" +
                       "id='" + id + '\'' +
                       ", status=" + status +
                       ", state=" + state +
                       ", attrs=" + attrs +
                       ", flowURI='" + flowURI + '\'' +
                       ", workQueue=" + workQueue +
                       ", rootTask=" + rootTask +
                       '}';
    }
}
