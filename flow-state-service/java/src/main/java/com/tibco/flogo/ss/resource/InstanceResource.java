package com.tibco.flogo.ss.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.handlers.CustomException;
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
@Path("/instances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InstanceResource
{
    private static final Logger LOG = Logger.getLogger(InstanceResource.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @GET()
    @Path("{processID}/status")
    public Map<String, String> getSnapshotStatus(@PathParam("processID") String processID)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getInstanceStatus(processID);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot MetaData: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    @Path("{processID}")
    public Map<String, Object> listSteps(@PathParam("processID") String processID)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listSteps(processID, true);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    @Path("{processID}/snapshot/{id}")
    public String getSnapshotStep(@PathParam("processID") String processID, @PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getSnapshotStep(processID, id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    @Path("{processID}/steps")
    public Map<String, Object> getInstanceSteps(@PathParam("processID") String processID)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listSteps(processID, false);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot MetaData: "
                                                                             + ex.getMessage());
        }
    }

    @GET()
    @Path("{processID}/metadata")
    public Map<String,String> processMetaData(@PathParam("processID") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getSnapshotMetadata(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot MetaData: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    @Path("snapshots")
    public List<Map<String,String>> listSnapshots()
    {
        try
        {
            Set<String> names = ConfigDaoImpl.getInstance().listSnapshots();

            List<Map<String, String>> results = new ArrayList<>(names.size());

            for (String processID : names)
            {
                results.add(ConfigDaoImpl.getInstance().getSnapshotMetadata(processID));
            }

            return results;
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot list: "
                                                                                     + ex.getMessage());
        }
    }

    @GET()
    public List<Map<String, String>> listInstanceStatuses()
    {
        try
        {
            return ConfigDaoImpl.getInstance().getInstancesMetadata();
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot list: "
                                                                             + ex.getMessage());
        }
    }

    @POST
    @Path("steps")
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

    @POST
    @Path("snapshot")
    public void postSnapshot(@Valid Map<String, Object> snapshotInfo)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().saveSnapshot((String)snapshotInfo.get("processID"), (Integer)snapshotInfo.get("id"),
                    (Integer)snapshotInfo.get("status"), (Integer)snapshotInfo.get("state"), snapshotInfo.get("snapshotData"));
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                      "Save for snapshot: " + (String)snapshotInfo.get("id") + " failed: "
                                      + ex.getMessage());
        }

        //doesn't account for snapshot overwrite
//        if (ret == 0)
//            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
//                                      "Save for snapshot: " + snapshotInfo.getId() + " failed ");
    }

    @DELETE()
    @Path("/{processID}")
    public void deleteProcess(@PathParam("processID") String processID)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeSnapshot(processID);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for snapshot: " + processID + " failed: "
                                                                             + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Snapshot: " + processID + " not found");
    }
}
