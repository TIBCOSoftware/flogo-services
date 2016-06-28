package com.tibco.flogo.ss.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mregiste on 3/3/2016.
 */
public class RootTask {
    private Integer id;
    private Integer taskID;
    private List<TaskData> taskDatas = new ArrayList<TaskData>();
    private List<Link> links = new ArrayList<Link>();

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
     * The taskID
     */
    public Integer getTaskID() {
        return taskID;
    }

    /**
     *
     * @param taskID
     * The taskID
     */
    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
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
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinkDatas(List<Link> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "RootTask{" +
                       "id=" + id +
                       ", taskID=" + taskID +
                       ", taskDatas=" + taskDatas +
                       ", links=" + links +
                       '}';
    }
}
