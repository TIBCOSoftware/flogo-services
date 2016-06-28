package com.tibco.flogo.dao;

import com.tibco.flogo.obj.FlowInfo;

import java.util.Map;
import java.util.Set;


/**
 * Created by mregiste on 2/10/2016.
 */
public interface DataDao
{
    String getFlow(String id);

    Map<String, String> saveFlow(String id, String name, String description, Object flow);

    Map<String, String> saveFlow(FlowInfo flowInfo);

    long removeFlow(String id);

    Set<String> listFlows();

    Map<String, String> getMetaData(String id);
}
