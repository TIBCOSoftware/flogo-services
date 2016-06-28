package com.tibco.flogo.ss.obj;

/**
 * Created by mregiste on 3/3/2016.
 */
public class Attribute {
    private String name;
    private String type;
    private String value;

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
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(String value) {
        this.value = value;
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
