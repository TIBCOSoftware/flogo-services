package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 2/21/2016.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LdChange
{
    private static final Logger LOG = LoggerFactory.getLogger(LdChange.class.getName());
    @JsonProperty("ChgType")
    private Integer chgType;
    @JsonProperty("ID")
    private Integer  id;
    @JsonProperty("LinkData")
    private LinkData linkData;

    public LdChange()
    {
    }

    public LdChange(Integer chgType, Integer id, LinkData linkData)
    {
        this.chgType = chgType;
        this.id = id;
        this.linkData = linkData;
    }

    /**
     * @return The chgType
     */
    public Integer getChgType()
    {
        return chgType;
    }

    /**
     * @param ChgType The chgType
     */
    public void setChgType(Integer ChgType)
    {
        this.chgType = ChgType;
    }

    /**
     * @return The Id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param Id The Id
     */
    public void setId(Integer Id)
    {
        this.id = Id;
    }

    /**
     * @return The linkData
     */
    public LinkData getLinkData()
    {
        return linkData;
    }

    /**
     * @param linkData The linkData
     */
    public void setLinkData(LinkData linkData)
    {
        this.linkData = linkData;
    }

    @Override
    public String toString() {
        return "LdChange{" +
                       "chgType=" + chgType +
                       ", Id=" + id +
                       ", linkData=" + linkData +
                       '}';
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch (IOException e)
        {
            LOG.error("Account JSON conversion error");
        }return null;
    }
}
