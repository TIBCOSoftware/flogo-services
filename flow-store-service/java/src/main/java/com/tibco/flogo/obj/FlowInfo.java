package com.tibco.flogo.obj;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by mregiste on 2/10/2016.
 */
public class FlowInfo
{
    String id;
    String name;
    String description;
    String flow;

    public FlowInfo()
    {}

    public FlowInfo(String id, String name, String description, String flow)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.flow = flow;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFlow()
    {
        return flow;
    }

    public void setFlow(String flow)
    {
        this.flow = flow;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String toJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString(this);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static void main(String[] args)
    {
        FlowInfo flowInfo = new FlowInfo();
        flowInfo.setDescription("description of flow");
        flowInfo.setFlow("{\"id\":1,\"typeId\":1,\"name\":\"test\",\"rootTask\":{\"id\":1,\"typeId\":1,\"name\":\"root\",\"tasks\":[{\"id\":2,\"typeId\":1,\"name\":\"a\",\"tasks\":null,\"links\":null},{\"id\":3,\"typeId\":1,\"name\":\"b\",\"tasks\":null,\"links\":null}],\"links\":[{\"id\":1,\"typeId\":1,\"name\":\"\",\"to\":3,\"from\":2}]}}");

        System.out.println("FlowInfo: " + flowInfo.toJson());
    }
}
