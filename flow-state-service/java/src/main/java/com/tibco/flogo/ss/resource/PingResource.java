package com.tibco.flogo.ss.resource;

import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * Created by mregiste on 2/9/2016.
 */
@Path("/ping")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PingResource
{
    private static final Logger LOG = Logger.getLogger(PingResource.class);

    /**
     * Called to check on server
     *
     * @return
     */
    @GET()
    public String replyToPing()
    {
        try
        {
            return "{\"status\":\"ok\"}";
        }
        catch (Exception e)
        {
            final String msg =
                    (e.getMessage() == null) ? "UnExpectedException" : e.getMessage().replaceAll("[\\r\\n]", "");
            LOG.fatal("PingResourceGet: ", e);
            return "{\"status\":\"PingResource: " + msg + "\"}";
        }
    }
}
