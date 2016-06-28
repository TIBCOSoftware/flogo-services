package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 3/3/2016.
 */
public class Link {
    private Integer state;
    private Object attrs;
    private Integer linkID;

    /**
     *
     * @return
     * The state
     */
    public Integer getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The attrs
     */
    public Object getAttrs() {
        return attrs;
    }

    /**
     *
     * @param attrs
     * The attrs
     */
    public void setAttrs(Object attrs) {
        this.attrs = attrs;
    }

    /**
     *
     * @return
     * The linkID
     */
    public Integer getLinkID() {
        return linkID;
    }

    /**
     *
     * @param linkID
     * The linkID
     */
    public void setLinkID(Integer linkID) {
        this.linkID = linkID;
    }

}
