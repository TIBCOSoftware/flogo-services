package com.tibco.flogo.ss.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.handlers.CustomException;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


/**
 * Created by mregiste on 2/10/2016.
 */
@Path("/steps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StepResource
{
    private static final Logger LOG = Logger.getLogger(StepResource.class);


    private final ObjectMapper mapper = new ObjectMapper();

    @GET()
    @Path("{id}")
    public Map<String, Object> listChanges(@PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listSteps(id, false);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                             + ex.getMessage());
        }
    }

    @GET()
    @Path("{id}/metadata")
    public Map<String, String> listChangeMetaData(@PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getStepMetadata(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                             + ex.getMessage());
        }
    }

    @GET()
    public List<Map<String, String>> listChange()
    {
        try
        {
            return ConfigDaoImpl.getInstance().listSteps();
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                             + ex.getMessage());
        }
    }

    @POST
    public void postChange(@Valid Map<String, Object> changeInfo)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().saveStep((String)changeInfo.get("processID"), (Integer)changeInfo.get("id"),
                    (Integer)changeInfo.get("state"), (Integer)changeInfo.get("status"), changeInfo.get("stepData"));
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                      "Save for stepData: " + changeInfo.get("id") + " failed: "
                                      + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                      "Save for stepData: " + changeInfo.get("id") + " failed");
    }

    @DELETE()
    @Path("{id}")
    public void deleteProcess(@PathParam("id") String id)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeStep(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for stepData: " + id + " failed: "
                                                                             + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Change: " + id + " not found");
    }
}
