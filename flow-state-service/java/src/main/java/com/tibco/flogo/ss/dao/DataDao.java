package com.tibco.flogo.ss.dao;

import com.tibco.flogo.ss.obj.SnapshotInfo;
import com.tibco.flogo.ss.obj.StepInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by mregiste on 2/10/2016.
 */
public interface DataDao
{
    Map<String, String> getSnapshot(String id);

    long saveSnapshot(String flowID, Integer id, Integer status, Integer state, Object snapshotObject);

    long removeSnapshot(String flowID);

    long removeFlow(String flowID);

    Set<String> listSnapshots();

    Set<String> listFlows();

    String drop();

    List<Map<String, String>> getFlowsMetadata();

    Map<String, String> getSnapshotMetadata(String flowID);

    Map<String, String> getSnapshotStatus(String flowID);

    Map<String, String> getFlowStatus(String flowID);

    // Steps

    Set<String> listAllFlowStepIds();

    List<String> listAllFlowStepIds(String flowID);

    List<Map<String, String>> listAllStepData();

    List<Map<String, String>> listFlowStepData(String flowID);

    Map<String, String> listStepData(String flowID);

    long saveStep(String flowID, Integer id, Integer state, Integer status, Object changeInfo);

    long removeSteps(String flowID);

    Map<String, String> getStep(String flowID);

    List<StepInfo> getStepInfo(String flowID);

    String getSnapshot(String flowID, String id);

    SnapshotInfo getSnapshotInfo(String flowID, Integer snapshotId);

    List<String> listAllFlowSnapshotIds(String flowId);

    List<SnapshotInfo> getSnapshotInfo(String flowID);

    // todo - mjr need to sort this out
    Map<String, Object> listSteps(String flowID, boolean withStatus);
}
