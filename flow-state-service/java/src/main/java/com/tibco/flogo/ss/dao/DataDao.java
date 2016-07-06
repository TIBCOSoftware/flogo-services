package com.tibco.flogo.ss.dao;

import com.tibco.flogo.ss.obj.StepInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by mregiste on 2/10/2016.
 */
public interface DataDao
{
    Map<String, String> getSnapshot(String processID, int version);

    long saveSnapshot(String processID, Integer id, Integer status, Integer state, Object snapshotObject);

    long removeSnapshot(String processID);

    Set<String> listSnapshots();

    List<Map<String, String>> getInstancesMetadata();

    Map<String, String> getSnapshotMetadata(String processID);

    Map<String, String> getSnapshotStatus(String processID);

    Map<String, String> getInstanceStatus(String processID);

    Map<String, Object> listSteps(String processID, boolean withStatus);

    List<Map<String, String>> listSteps();

    long saveStep(StepInfo stepInfo);

    long saveStep(String processID, Integer id, Integer state, Integer status, Object changeInfo);

    long removeStep(String processID);

    Map<String, String> getStepMetadata(String processID);

    List<StepInfo> getStepInfo(String processID);

    String getSnapshotStep(String processID, String id);
}
