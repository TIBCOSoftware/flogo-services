package com.tibco.flogo.ss.util;

import com.tibco.flogo.ss.obj.StepInfo;

import java.util.Comparator;

/**
 * Created by mregiste on 7/18/2016.
 */
public class StepIdComp implements Comparator<StepInfo> {

    @Override
    public int compare(StepInfo e1, StepInfo e2) {
        if(e1.getId() < e2.getId()){
            return 1;
        } else {
            return -1;
        }
    }
}
