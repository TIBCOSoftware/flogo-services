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
@Path("/snapshots")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SnapshotResource
{
    private static final Logger LOG = Logger.getLogger(SnapshotResource.class);

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retrieves snapshot
     * @param id - <flowid>:<ssid>
     * @return
     */
    @GET()
    @Path("{id}")
    public Map<String, String> getSnapshotStep(@PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().getSnapshot(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                                     + ex.getMessage());
        }
    }


    /**
     * Retrieves metadata for snapshot
     * @param id - id - <flowid>:<ssid>
     * @return
     */
    @GET()
    @Path("{id}/metadata")
    public Map<String,String> flowMetaData(@PathParam("id") String id)
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
    public List<Map<String,String>> listSnapshots()
    {
        try
        {
            Set<String> names = ConfigDaoImpl.getInstance().listSnapshots();

            List<Map<String, String>> results = new ArrayList<>(names.size());

            for (String flowID : names)
            {
                results.add(ConfigDaoImpl.getInstance().getSnapshotMetadata(flowID));
            }

            return results;
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return snapshot list: "
                                                                                     + ex.getMessage());
        }
    }


    @POST
    @Path("snapshot")
    public void postSnapshot(@Valid Map<String, Object> snapshotInfo)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().saveSnapshot((String)snapshotInfo.get("flowID"), (Integer)snapshotInfo.get("id"),
                    (Integer)snapshotInfo.get("status"), (Integer)snapshotInfo.get("state"), snapshotInfo.get("snapshotData"));
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                      "Save for snapshot: " + (String)snapshotInfo.get("id") + " failed: "
                                      + ex.getMessage());
        }
    }

    @DELETE()
    @Path("/{flowID}")
    public void deleteFlow(@PathParam("flowID") String flowID)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeSnapshot(flowID);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for snapshot: " + flowID + " failed: "
                                                                             + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Snapshot: " + flowID + " not found");
    }
}
