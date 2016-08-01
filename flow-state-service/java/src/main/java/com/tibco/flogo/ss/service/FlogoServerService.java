package com.tibco.flogo.ss.service;

import com.tibco.flogo.ss.dao.impl.ConfigDaoImpl;
import com.tibco.flogo.ss.resource.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.log4j.Logger;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;


/**
 * Created by mregiste on 2/9/2016.
 */
public class FlogoServerService extends Application<FlogoServiceConfiguration>
{
    private static final Logger LOG = Logger.getLogger(FlogoServerService.class);

    public static void main(String[] args)
            throws Exception
    {
        new FlogoServerService().run(args);
    }

    @Override
    public String getName()
    {
        return "flogo-state-service";
    }

    @Override
    public void initialize(Bootstrap<FlogoServiceConfiguration> bootstrap)
    {

    }

    @Override
    public void run(FlogoServiceConfiguration configuration, Environment environment)
            throws Exception
    {
        PropertyClient propertyClient = configuration.getPropertyFactory().build();
        boolean isConnected;

        // setup storage
        isConnected = ConfigDaoImpl.init(propertyClient);
        if (!isConnected)
            LOG.fatal("No database!");

        // ping for health check
        environment.jersey().register(new PingResource());

        // general services
        environment.jersey().register(new InstanceResource());
        environment.jersey().register(new StepResource());
        environment.jersey().register(new SnapshotResource());
        environment.jersey().register(new FlowResource());

        /*
         * Filters
         */
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*");
        // filterHolder.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin, authorization");
        filter.setInitParameter("allowedHeaders", "Authorization,Content-Type,X-Api-Key,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,POST,PUT,DELETE,OPTIONS");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");

//        environment.jersey().property(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, CatchAllRequestFilter.class.getName());
    }
}
