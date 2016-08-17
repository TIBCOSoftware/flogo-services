package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mregiste on 7/18/2016.
 */
public class RollUpObj {
    private static final Logger LOG = LoggerFactory.getLogger(RollUpObj.class.getName());
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("flowUri")
    private String flowUri;
    @JsonProperty("snapshot")
    private SnapshotData snapshot = new SnapshotData();
    // list of work items, tasks, links and attributes so we know not to add them if they were already processed
    private Set<Integer> ignoredTaskIds = new HashSet<>();
    private Set<Integer> ignoredLinkIds = new HashSet<>();
    private Set<Integer> ignoredWorkItemIds = new HashSet<>();
    private Set<String> ignoredAttrs = new HashSet<>();

    public RollUpObj(Integer id, Integer status, Integer state, String flowUri, SnapshotData snapshotData) {
        this.id = id;
        this.status = status;
        this.state = state;
        this.flowUri = flowUri;
        this.snapshot = snapshotData;
    }

    public RollUpObj() {
        // todo - where do these values come from?
        snapshot.getRootTaskEnv().setId(1); // loop id
        snapshot.getRootTaskEnv().setTaskId(1);
    }

    public void setRootTaskEnvId(Integer id) {
        snapshot.getRootTaskEnv().setId(1);
    }

    public void setRootTaskEnvTaskId(Integer taskId) {
        snapshot.getRootTaskEnv().setTaskId(1);
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

    public void setSnapshotStatus(Integer status) {
        snapshot.setStatus(status);
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public void setSnapshotState(Integer state) {
        snapshot.setState(status);
    }

    public String getFlowUri() {
        return flowUri;
    }

    public void setFlowUri(String flowUri) {
        this.flowUri = flowUri;
    }

    public void setSnapshotFlowId(String flowUri) {
        snapshot.setId(flowUri);
    }

    public void setSnapshotFlowUri(String flowUri) {
        snapshot.setFlowUri(flowUri);
    }

    public Set<Attribute> getAttrs() {
        return snapshot.getAttrs();
    }

    public void setAttrs(Set<Attribute> attrs) {
        snapshot.setAttrs(attrs);
    }

    public void addAttr(Attribute attribute) {
        if(attribute != null) {
            if(!ignoredAttrs.contains(attribute.getName()))
                snapshot.getAttrs().add(attribute);
        }
    }

    public void updateAttr(Attribute attribute) {
        if(attribute != null) {
            ignoredAttrs.add(attribute.getName());
            snapshot.getAttrs().add(attribute);
        }
    }

    public Attribute getAttr(String name) {
        for (Attribute attr : snapshot.getAttrs()) {
            if(attr.getName().equals(name))
                return attr;
        }
        return null;
    }

    public boolean attrExists(String name) {
        for (Attribute attr : snapshot.getAttrs()) {
            if(attr.getName().equals(name))
                return true;
        }
        return false;
    }

    public void setWorkQueue(Set<WorkItem> workQueue) {
        snapshot.setWorkQueue(workQueue);
    }

    public void addWorkQueueItem(WorkItem workItem) {
        if(!ignoredWorkItemIds.contains(workItem.getId()))
            snapshot.getWorkQueue().add(workItem);
    }

    public void removeWorkQueueItem(WorkItem workItem) {
        ignoredWorkItemIds.add(workItem.getId());
    }

    public void updateWorkQueueItem(WorkItem workItem) {
        if(!ignoredWorkItemIds.contains(workItem.getId())) {
            ignoredWorkItemIds.add(workItem.getId());
            getSnapshot().getWorkQueue().add(workItem);
        }
    }

    public void setRooTaskEnv(RootTaskEnv rooTaskEnv) {
        snapshot.setRootTaskEnv(rooTaskEnv);
    }

    public void addTask(TaskData taskData)
    {
        if(taskData != null) {
            if(!ignoredTaskIds.contains(taskData.getTaskId()))
                snapshot.getRootTaskEnv().addTaskData(taskData);
        }
    }

    public void updateTask(TaskData taskData)
    {
        if(taskData != null) {
            if(!ignoredTaskIds.contains(taskData.getTaskId())) {
                ignoredTaskIds.add(taskData.getTaskId());
                snapshot.getRootTaskEnv().removeTaskData(taskData);
                snapshot.getRootTaskEnv().addTaskData(taskData);
            }
        }
    }

    public void removeTask(Integer taskId)
    {
        if(taskId != null) {
            snapshot.getRootTaskEnv().removeTaskData(taskId);
            ignoredTaskIds.add(taskId);
        }
    }

    public void addLink(LinkData linkData)
    {
        if(linkData != null) {
            if(!ignoredLinkIds.contains(linkData.getLinkId()))
                snapshot.getRootTaskEnv().addLinkData(linkData);
        }
    }

    public void updateLink(LinkData linkData)
    {
        if(linkData != null) {
            if(!ignoredLinkIds.contains(linkData.getLinkId())) {
                ignoredLinkIds.add(linkData.getLinkId());
                snapshot.getRootTaskEnv().removeLinkData(linkData);
                snapshot.getRootTaskEnv().addLinkData(linkData);
            }

        }
    }

    public void removeLink(Integer linkId)
    {
        if(linkId != null) {
            snapshot.getRootTaskEnv().removeLinkData(linkId);
            ignoredLinkIds.add(linkId);
        }
    }

    public void removeAttr(Attribute attribute)
    {
        if(attribute != null) {
            snapshot.removeAttribute(attribute);
            ignoredAttrs.add(attribute.getName());
        }
    }

    public boolean taskExists(Integer taskId)
    {
        List<TaskData> taskDatas = snapshot.getRootTaskEnv().getTaskDatas();
        if(!taskDatas.isEmpty()) {
            for (TaskData data : taskDatas) {
                if (data.getTaskId().equals(taskId))
                    return true;
            }
        }
        return false;
    }

    public SnapshotData getSnapshot() {
        return snapshot;
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
            LOG.error("Account JSON conversion error");
        }return null;
    }
}
