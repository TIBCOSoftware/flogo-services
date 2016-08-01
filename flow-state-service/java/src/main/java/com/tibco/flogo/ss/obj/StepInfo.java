package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by mregiste on 2/21/2016.
 */
public class StepInfo {
    private static final Logger LOG = LoggerFactory.getLogger(StepInfo.class.getName());
    public static final String FLOW_ID = "flowID";
    public static final String ID = "id";
    public static final String STEP_DATA = "stepData";
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String STATE = "state";

    public String flowID;
    public Integer id;
    @JsonIgnore
    public StepData stepData;
    public Integer status;
    public Integer state;
    public String date;

    public StepInfo() {
    }

    public StepInfo(String flowID, Integer id, Integer status, StepData change) {
        this.flowID = flowID;
        this.id = id;
        this.status = status;
        this.stepData = change;
    }

    public Integer getId() {
        return id;
    }

    public String getFlowId() {
        return flowID;
    }

    public void setFlowId(String flowID) {
        this.flowID = flowID;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public StepData getStepData() {
        return stepData;
    }

    public void setStepData(StepData stepData) {
        this.stepData = stepData;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString()
    {
        return "StepInfo{processID='" + this.flowID + '\'' + ", id='" + this.id + '\'' + ", stepData=" + this.stepData + ", status='" + this.status + '\'' + ", state='" + this.state + '\'' + '}';
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
