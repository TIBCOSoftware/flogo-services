package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mregiste on 3/3/2016.
 */
public class SnapshotData {
    public static final String WORK_QUEUE = "workQueue";
    public static final String ROOT_TASK_ENV = "rootTaskEnv";
    public static final String ATTRS = "attrs";

    private String id;
    private Integer status;
    private Integer state;
    private Set<Attribute> attrs = new HashSet<>();
    private String flowUri;
    private Set<WorkItem> workQueue = new HashSet<>();
    private RootTaskEnv rootTaskEnv = new RootTaskEnv();

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
    public Set<Attribute> getAttrs() {
        return attrs;
    }

    /**
     *
     * @param attrs
     * The attrs
     */
    public void setAttrs(Set<Attribute> attrs) {
        this.attrs = attrs;
    }

    public void removeAttribute(Attribute attribute) {
        attrs.remove(attribute);
    }

    /**
     *
     * @return
     * The flowUri
     */
    public String getFlowUri() {
        return flowUri;
    }

    /**
     *
     * @param flowUri
     * The flowURI
     */
    public void setFlowUri(String flowUri) {
        this.flowUri = flowUri;
    }

    /**
     *
     * @return
     * The workQueue
     */
    public Set<WorkItem> getWorkQueue() {
        return workQueue;
    }

    /**
     *
     * @param workQueue
     * The workQueue
     */
    public void setWorkQueue(Set<WorkItem> workQueue) {
        this.workQueue = workQueue;
    }

    public void removeWorkItem(WorkItem workItem) {
        for (WorkItem item : workQueue) {
            if(item.getId().equals(workItem.getId()) && item.getExecType().equals(workItem.getExecType())
                    && item.getCode().equals(workItem.getCode())) {
                workQueue.remove(workItem);
            }
        }
    }

    /**
     *
     * @return
     * The rootTaskEnv
     */
    public RootTaskEnv getRootTaskEnv() {
        return rootTaskEnv;
    }

    /**
     *
     * @param rootTask
     * The rootTaskEnv
     */
    public void setRootTaskEnv(RootTaskEnv rootTask) {
        this.rootTaskEnv = rootTask;
    }

    @Override
    public String toString() {
        return "SnapshotData{" +
                       "id='" + id + '\'' +
                       ", status=" + status +
                       ", state=" + state +
                       ", attrs=" + attrs +
                       ", flowURI='" + flowUri + '\'' +
                       ", workQueue=" + workQueue +
                       ", rootTaskEnv=" + rootTaskEnv +
                       '}';
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
