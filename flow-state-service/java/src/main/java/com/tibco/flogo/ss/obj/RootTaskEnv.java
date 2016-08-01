package com.tibco.flogo.ss.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mregiste on 3/3/2016.
 */
public class RootTaskEnv {
    private Integer id;
    private Integer taskId;
    private List<TaskData> taskDatas = new ArrayList<>();
    private List<LinkData> linkDatas = new ArrayList<>();

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
     * The taskId
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     *
     * @param taskId
     * The taskId
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     *
     * @return
     * The taskDatas
     */
    public List<TaskData> getTaskDatas() {
        return taskDatas;
    }

    /**
     *
     * @param taskDatas
     * The taskDatas
     */
    public void setTaskDatas(List<TaskData> taskDatas) {
        this.taskDatas = taskDatas;
    }

    /**
     *
     * @return
     * The links
     */
    public List<LinkData> getLinkDatas() {
        return linkDatas;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinkDatas(List<LinkData> links) {
        this.linkDatas = links;
    }

    public void addTaskData(TaskData taskData) {
        taskDatas.add(taskData);
    }

    public boolean removeTaskData(TaskData taskData) {
        return taskDatas.remove(taskData);
    }

    public void removeTaskData(Integer taskId) {
        for (TaskData taskData : taskDatas) {
            if(taskData.getTaskId().equals(taskId))
            {
                taskDatas.remove(taskData);
                break;
            }
        }
    }

    public boolean removeLinkData(LinkData linkData) {
        return linkDatas.remove(linkData);
    }

    public void removeLinkData(Integer linkId) {
        for (LinkData linkData : linkDatas) {
            if(linkData.getLinkId().equals(linkId))
            {
                linkDatas.remove(linkData);
                break;
            }
        }
    }

    public void addLinkData(LinkData linkData) {
        linkDatas.add(linkData);
    }

    @Override
    public String toString() {
        return "RootTask{" +
                       "id=" + id +
                       ", taskId=" + taskId +
                       ", taskDatas=" + taskDatas +
                       ", links=" + linkDatas +
                       '}';
    }
}
