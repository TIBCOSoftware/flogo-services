package com.tibco.flogo.handlers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by mregiste on 2/18/2016.
 */
public class CustomException extends WebApplicationException
{
    public CustomException(Response.Status status, String message)
    {
        super(Response.status(status)
                      .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
