package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 6/27/2016.
 */
public class TaskTracker {
    private boolean done;
    private TaskData taskData;

    public TaskTracker(TaskData taskData, boolean done) {
        this.taskData = taskData;
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }
}
