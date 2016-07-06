package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mregiste on 2/21/2016.
 */
public class StepInfo {
    private static final Logger LOG = LoggerFactory.getLogger(StepInfo.class.getName());

    public static final String PROCESS_ID = "processID";
    public static final String ID = "id";
    public static final String STEP_DATA = "stepData";
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String STATE = "state";

    public String processID;
    public String id;
    public StepData stepData;
    public String status;
    public String state;

    public StepInfo() {
    }

    public StepInfo(String processID, String id, String status, StepData stepData) {
        this.processID = processID;
        this.id = id;
        this.status = status;
        this.stepData = stepData;
    }

    public String getId() {
        return id;
    }

    public String getProcessId() {
        return processID;
    }

    public void setProcessId(String processID) {
        this.processID = processID;
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

    public StepData getStepData() {
        return stepData;
    }

    public void setStepData(StepData stepData) {
        this.stepData = stepData;
    }

    @Override
    public String toString() {
        return "StepInfo{" +
                "processID='" + processID + '\'' +
                ", id='" + id + '\'' +
                ", stepData=" + stepData +
                ", status='" + status + '\'' +
                ", state='" + state + '\'' +
                '}';
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
            return null;
        }
    }
}
