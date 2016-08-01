package com.tibco.flogo.ss.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.RedisDataDao;
import com.tibco.flogo.ss.obj.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by mregiste on 7/18/2016.
 */
public class RollUp {
    private static final Logger LOG = LoggerFactory.getLogger(RollUp.class.getName());

    public static RollUpObj rollUp(String flowId, Integer stepId)
    {
        RollUpObj rollUpObj = new RollUpObj();
        List<StepInfo> steps = RedisDataDao.getInstance().getStepInfo(flowId);

        Collections.sort(steps,new StepIdComp());
        for (StepInfo step : steps)
        {
            StepData stepData = step.getStepData();
            if(step.getId() <= stepId) {
                if (step.getId().equals(stepId)) {
                    rollUpObj.setId(step.getId());
                    rollUpObj.setState(step.getState());
                    rollUpObj.setStatus(step.getStatus());
                    rollUpObj.setFlowUri(step.getFlowId());
                }

                addWorkItem(stepData, rollUpObj);
                addTaskAndLinkDatas(stepData, rollUpObj);
                addAttribute(stepData, rollUpObj);
            }
        }

        return rollUpObj;
    }

    public static Map<String, Object> rollUp(String id) {
        if(id != null && !id.isEmpty()) {
            String[] tokens = id.split(":");
            if(tokens.length != 2) {
                throw new IllegalArgumentException("Invalid id format: " +id +" should be <flowid>:<sid>");
            }

            RollUpObj rollUpObj = rollUp(tokens[0], Integer.valueOf(tokens[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(rollUpObj, Map.class);
        }
        else {
            throw new IllegalArgumentException("Invalid id format: " +id +" should be <flowid>:<sid>");
        }
    }

    // helpers
    private static void addAttribute(StepData stepData, RollUpObj rollUpObj)
    {
        List<Attr> attrs = stepData.getAttrs();
        if(attrs != null) {
            for (Attr attr : attrs) {
                LOG.debug("Attribute type: " +attr.getChgType() +" name: " +attr.getAttribute().getName());
                if(attr.getChgType().equals(1))
                    rollUpObj.addAttr(attr.getAttribute());
                else if(attr.getChgType().equals(2))
                    rollUpObj.updateAttr(attr.getAttribute());
                else if(attr.getChgType().equals(3))
                    rollUpObj.removeAttr(attr.getAttribute());
            }
        }
    }

    private static void addWorkItem(StepData stepData, RollUpObj rollUpObj)
    {
        List<WqChange> wqChanges = stepData.getWqChanges();
        if(wqChanges != null) {
            for (WqChange wqChange : wqChanges) {
                Integer id = wqChange.getId();
                Integer chgType = wqChange.getChgType();
                LOG.debug("Work Item type: " + chgType + " id: " + id);
                // add
                if (chgType.equals(1)) {
                    rollUpObj.addWorkQueueItem(wqChange.getWorkItem());
                } else if (chgType.equals(2))
                    rollUpObj.updateWorkQueueItem(wqChange.getWorkItem());
                else if (chgType.equals(3)) { //remove
                    rollUpObj.removeWorkQueueItem(wqChange.getWorkItem());
                }
            }
        }
    }

    private static void addTaskAndLinkDatas(StepData stepData, RollUpObj rollUpObj)
    {
        // task changes
        List<TdChange> tdChanges = stepData.getTdChanges();
        if(tdChanges != null) {
            for (TdChange tdChange : tdChanges) {
                // remove task
                if (tdChange.getChgType().equals(3))
                    rollUpObj.removeTask(tdChange.getId());
                if (tdChange.getChgType().equals(2))
                    rollUpObj.addTask(tdChange.getTaskData());
                if (tdChange.getChgType().equals(1))
                    rollUpObj.updateTask(tdChange.getTaskData());
            }
        }

        // link changes
        List<LdChange> ldChanges = stepData.getLdChanges();
        if(ldChanges != null) {
            for (LdChange ldChange : ldChanges) {
                // remove link
                if (ldChange.getChgType().equals(3))
                    rollUpObj.removeLink(ldChange.getId());
                if (ldChange.getChgType().equals(2))
                    rollUpObj.addLink(ldChange.getLinkData());
                if (ldChange.getChgType().equals(1))
                    rollUpObj.updateLink(ldChange.getLinkData());
            }
        }
    }
}
