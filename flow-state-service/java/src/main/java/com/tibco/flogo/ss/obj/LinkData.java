package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 3/3/2016.
 */
public class LinkData {
    private Integer state;
    private Object attrs;
    private Integer linkId;

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
     * The linkId
     */
    public Integer getLinkId() {
        return linkId;
    }

    /**
     *
     * @param linkId
     * The linkId
     */
    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkData linkData = (LinkData) o;

        return linkId.equals(linkData.linkId);

    }

    @Override
    public int hashCode() {
        return linkId.hashCode();
    }
}
