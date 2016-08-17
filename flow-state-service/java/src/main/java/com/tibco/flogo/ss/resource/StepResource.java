package com.tibco.flogo.ss.resource;

import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.handlers.CustomException;
import com.tibco.flogo.ss.obj.RollUpObj;
import com.tibco.flogo.ss.obj.SnapshotData;
import com.tibco.flogo.ss.util.RollUp;
import org.apache.log4j.Logger;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static com.tibco.flogo.ss.util.RollUp.rollUp;


/**
 * Created by mregiste on 2/10/2016.
 */
@Path("/steps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StepResource
{
    private static final Logger LOG = Logger.getLogger(StepResource.class);

    /**
     * Retrieves the roll up from the first step to the specified step
     * @param id - <flowid>:<sid>
     * @return
     */
    @GET()
    @Path("{id}/rollup")
    public Map<String, Object> listRollUp(@PathParam("id") String id)
    {
        try
        {
            return rollUp(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return step rollup: "
                                                                             + ex.getMessage());
        }
    }

    @GET()
    @Path("{id}/rollup/snapshot")
    public String listRollUpMetaData(@PathParam("id") String id)
    {
        try
        {
            if(id != null && !id.isEmpty()) {
                String[] tokens = id.split(":");
                if(tokens.length != 2) {
                    throw new IllegalArgumentException("Invalid id format: " +id +" should be <flowid>:<sid>");
                }

                RollUpObj rollUpObj = RollUp.rollUp(tokens[0], Integer.valueOf(tokens[1]));
                SnapshotData snapshotData = rollUpObj.getSnapshot();
                return snapshotData.toJson();
            }

            return "";
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return step rollup: "
                    + ex.getMessage());
        }
    }

    /**
     * Retrieves all steps to all flows
     * @return
     */
    @GET()
    public List<Map<String, String>> listAllStepData()
    {
        try
        {
            return ConfigDaoImpl.getInstance().listAllStepData();
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                                                                             + ex.getMessage());
        }
    }

    /**
     * Retrieves a step by id
     * @param id - <flowid>:<sid>
     * @return
     */
    @GET()
    @Path("{id}/stepdata")
    public Map<String, String> listStepData(@PathParam("id") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listStepData(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                    + ex.getMessage());
        }
    }

    /**
     * Retreives all steps to a flow
     * @param id
     * @return
     */
    @GET()
    @Path("flow/{flowid}/stepdata")
    public List<Map<String, String>> listFlowStepData(@PathParam("flowid") String id)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listFlowStepData(id);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return stepData list: "
                    + ex.getMessage());
        }
    }


    @GET()
    @Path("{flowid}/stepids")
    public List<String> listAllFlowStepIds(@PathParam("flowid") String flowid)
    {
        try
        {
            return ConfigDaoImpl.getInstance().listAllFlowStepIds(flowid);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to return step ids for flow: "
                    + ex.getMessage());
        }
    }

    @POST
    public void postChange(@Valid Map<String, Object> changeInfo)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().saveStep((String)changeInfo.get("flowID"), (Integer)changeInfo.get("id"),
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
    @Path("{flowid}")
    public void deleteSteps(@PathParam("flowid") String flowid)
    {
        long ret;
        try
        {
            ret = ConfigDaoImpl.getInstance().removeSteps(flowid);
        }
        catch (Exception ex)
        {
            throw new CustomException(Response.Status.INTERNAL_SERVER_ERROR, "Delete for stepData: " + flowid + " failed: "
                                                                             + ex.getMessage());
        }

        if (ret == 0)
            throw new CustomException(Response.Status.NOT_FOUND, "Step: " + flowid + " not found");
    }
}
