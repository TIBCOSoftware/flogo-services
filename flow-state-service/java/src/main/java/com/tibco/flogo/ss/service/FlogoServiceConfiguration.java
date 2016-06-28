package com.tibco.flogo.ss.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * Created by mregiste on 2/9/2016.
 */
public class FlogoServiceConfiguration extends Configuration
{
    @Valid
    @NotNull
    private PropertyFactory properties = new PropertyFactory();

    @JsonProperty("appProperties")
    public PropertyFactory getPropertyFactory()
    {
        return properties;
    }

    @JsonProperty("appProperties")
    public void setPropertyFactory(PropertyFactory factory)
    {
        this.properties = factory;
    }
}
