package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 6/27/2016.
 */
public class LinkTracker {
    private boolean done;
    private Link link;

    public LinkTracker(Link link, boolean done) {
        this.link = link;
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
