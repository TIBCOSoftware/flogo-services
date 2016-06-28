package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */
public class StepInfo {
    public static final String FLOW_ID = "flowID";
    public static final String ID = "id";
    public static final String STEP_DATA = "stepData";
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String STATE = "state";

    public String flowID;
    public String id;
    public String stepData;
    public String status;
    public String state;

    public StepInfo() {
    }

    public StepInfo(String flowID, String id, String status, String change) {
        this.flowID = flowID;
        this.id = id;
        this.status = status;
        this.stepData = change;
    }

    public String getId() {
        return id;
    }

    public String getFlowId() {
        return flowID;
    }

    public void setFlowId(String flowID) {
        this.flowID = flowID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStepData() {
        return stepData;
    }

    public void setStepData(String stepData) {
        this.stepData = stepData;
    }
}
