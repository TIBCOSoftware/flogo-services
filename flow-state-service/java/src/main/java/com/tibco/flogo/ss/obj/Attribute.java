package com.tibco.flogo.ss.obj;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by mregiste on 3/3/2016.
 */
public class Attribute {
    private String name;
    private String type;
    private Object value;

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(Object value) {
        this.value = value;
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
            return null;
        }
    }

    @Override
    public String toString() {
        return "Attribute{" +
                       "name='" + name + '\'' +
                       ", type='" + type + '\'' +
                       ", value='" + value + '\'' +
                       '}';
    }
}
