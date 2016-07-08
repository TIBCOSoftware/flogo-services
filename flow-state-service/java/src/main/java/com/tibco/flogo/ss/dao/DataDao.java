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
    Map<String, String> getSnapshot(String flowID, int version);

    long saveSnapshot(String flowID, Integer id, Integer status, Integer state, Object snapshotObject);

    long removeSnapshot(String flowID);

    Set<String> listSnapshots();

    List<Map<String, String>> getInstancesMetadata();

    Map<String, String> getSnapshotMetadata(String flowID);

    Map<String, String> getSnapshotStatus(String flowID);

    Map<String, String> getInstanceStatus(String flowID);

    Map<String, Object> listSteps(String flowID, boolean withStatus);

    List<Map<String, String>> listSteps();

    long saveStep(StepInfo stepInfo);

    long saveStep(String flowID, Integer id, Integer state, Integer status, Object changeInfo);

    long removeStep(String flowID);

    Map<String, String> getStepMetadata(String flowID);

    String getSnapshotStep(String flowID, String id);
}
