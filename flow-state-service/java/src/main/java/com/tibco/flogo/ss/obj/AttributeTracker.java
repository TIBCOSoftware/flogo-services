package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 6/27/2016.
 */
public class AttributeTracker {
    private boolean done;
    private Attribute attribute;

    public AttributeTracker(boolean done, Attribute attribute) {
        this.done = done;
        this.attribute = attribute;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
}
