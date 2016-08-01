package com.tibco.flogo.ss.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.handlers.CustomException;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


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
    @Path("{flowID}/status")
    public Map<String, String> getSnapshotStatus(@PathParam("flowID") String flowID)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getFlowStatus(flowID);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot status: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    public List<Map<String, String>> listFlowStatuses()
    {
        try
        {
            return ConfigDaoImpl.getInstance().getFlowsMetadata();
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot list: "
                    + ex.getMessage());
        }
    }

    @DELETE()
    @Path("{flowID}")
    public void deleteFlow(@PathParam("flowID") String flowId)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeFlow(flowId);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for flow: " + flowId + " failed: "
                    + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Flow: " + flowId + " not found");
    }
}
