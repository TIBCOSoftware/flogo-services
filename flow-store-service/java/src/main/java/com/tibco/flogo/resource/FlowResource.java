package com.tibco.flogo.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.handlers.CustomException;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by mregiste on 2/10/2016.
 */
@Path("/flows")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlowResource
{
    private static final Logger LOG = Logger.getLogger(FlowResource.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @GET()
    @Path("{id}")
    public Map<String, Object> getFlow(@PathParam("id") String id)
    {
        try
        {
            String flowJson = ConfigDaoImpl.getInstance().getFlow(id);
            if (flowJson != null)
            {
                return mapper.readValue(flowJson, new TypeReference<Map<String, Object>>()
                {
                });
            }
            else
                throw new CustomException(Response.Status.NOT_FOUND, "Failed to get flow: " + id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to get flow: " + id + ":"
                                                                             + ex.getMessage());
        }
    }

    @GET()
    @Path("{id}/metadata")
    public Map<String, String> flowMetaData(@PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getMetaData(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return flow MetaData: "
                                                                             + ex.getMessage());
        }
    }

    @GET()
    public List<Map<String, String>> listFlows()
    {
        try
        {
            Set<String> flowIds = ConfigDaoImpl.getInstance().listFlows();
            List<Map<String, String>> results = new ArrayList<>(flowIds.size());

            for (String flowId : flowIds)
            {
                results.add(ConfigDaoImpl.getInstance().getMetaData(flowId));
            }

            return results;
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return flow list: "
                                                                             + ex.getMessage());
        }
    }

    @POST
    public Map<String, String> postFlow(@Valid Map<String, Object> flow)
    {


        System.out.println("");
        try
        {
            return ConfigDaoImpl.getInstance().saveFlow((String)flow.get("id"), (String)flow.get("name"), (String)flow.get("description"),
                                                           flow.get("flow"));
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                      "Save for flow: " + (String)flow.get("name") + " failed: "
                                      + ex.getMessage());
        }
    }

    @DELETE()
    @Path("{id}")
    public void deleteFlow(@PathParam("id") String id)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeFlow(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for flow: " + id + " failed: "
                                                                             + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Flow: " + id + " not found");
    }
}
