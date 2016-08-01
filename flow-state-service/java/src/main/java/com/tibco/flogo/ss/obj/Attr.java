package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mregiste on 7/20/2016.
 */
public class Attr {
    private static final Logger LOG = LoggerFactory.getLogger(WqChange.class.getName());
    @JsonProperty("ChgType")
    private Integer  chgType;
    @JsonProperty("Attribute")
    private Attribute attribute;

    public Integer getChgType() {
        return chgType;
    }

    public void setChgType(Integer chgType) {
        this.chgType = chgType;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
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
