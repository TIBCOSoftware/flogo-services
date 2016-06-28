package com.tibco.flogo.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.dao.DataDao;
import com.tibco.flogo.obj.FlowInfo;
import com.tibco.flogo.service.PropertyClient;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by mregiste on 2/10/2016.
 */
public class RedisDataDao implements DataDao
{
    AtomicLong m_id = new AtomicLong();
    private static final Logger LOG = Logger.getLogger(RedisDataDao.class);
    private static JedisPool m_pool;

    public boolean config(PropertyClient propertyClient)
    {
        String host = propertyClient.getHost();

        if (System.getenv("FLOGO_REDIS_HOST") != null)
        {
            host = System.getenv("FLOGO_REDIS_HOST");
        }

        m_pool = new JedisPool(new JedisPoolConfig(), host, propertyClient.getPort(), propertyClient.getTimeOut());
        final Jedis jedis = m_pool.getResource();
        if (!jedis.isConnected())
        {
            LOG.fatal("Redis connect failed");
            return false;
        }
        else
            return true;
    }

    @Override
    public String getFlow(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.hget("flow:" + id, "flow");
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }


    private static boolean nullOrEmpty(String value)
    {
        return value == null || value.isEmpty();
    }

    @Override
    public Map<String, String> saveFlow(FlowInfo flowInfo)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            String id = nullOrEmpty(flowInfo.getId()) ? String.valueOf(m_id.getAndIncrement()) : flowInfo.getId();

            Map<String, String> flow = new HashMap<String, String>();
            flow.put("creationDate", String.valueOf(new Date()));
            flow.put("id", id);
            flow.put("name", flowInfo.getName());
            flow.put("description", flowInfo.getDescription());
            flow.put("flow", flowInfo.getFlow());

            Transaction t = jedis.multi();
            Response<String> addProcResp = t.hmset("flow:" + id, flow);
            Response<Long> addProcListResp = t.sadd("flows", String.valueOf(id));
            t.exec();
            long ret = addProcListResp.get();
            if (ret > 0)
            {
                Map<String, String> metaData = getMetaData(id);
                if (metaData != null)
                    metaData.put("id", id);
                return metaData;
            }

            return null;
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    @Override
    public Map<String, String> saveFlow(String id, String name, String description, Object flowMap)
    {
        Jedis jedis = null;
        try
        {
            String jsonFlow;
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                jsonFlow = mapper.writeValueAsString(flowMap);
            }
            catch (IOException e)
            {
                return null;
            }
            jedis = m_pool.getResource();

            id = nullOrEmpty(id) ? String.valueOf(m_id.getAndIncrement()) : id;

            Map<String, String> flow = new HashMap<String, String>();
            flow.put("creationDate", String.valueOf(new Date()));
            flow.put("id", id);
            flow.put("name", name);
            flow.put("description", description);
            flow.put("flow", jsonFlow);

            Transaction t = jedis.multi();
            Response<String> addProcResp = t.hmset("flow:" + id, flow);
            Response<Long> addProcListResp = t.sadd("flows", id);
            t.exec();
            long ret = addProcListResp.get();
            if (ret > 0)
            {
                Map<String, String> metaData = getMetaData(id);
                if (metaData != null)
                    metaData.put("id", id);
                return metaData;
            }

            return null;
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    @Override
    public long removeFlow(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            Transaction t = jedis.multi();
            Response<Long> remProcListResp = t.srem("flows", id);
            Response<Long> remProcResp = t.del("flow:" + id);
            t.exec();
            return remProcResp.get();
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    @Override
    public Set<String> listFlows()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.sinter("flows");
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    @Override
    public Map<String, String> getMetaData(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String desc = jedis.hget("flow:" + id, "description");
            String name = jedis.hget("flow:" + id, "name");
            String creationDate = jedis.hget("flow:" + id, "creationDate");

            Map<String, String> metaData = new HashMap<String, String>();
            metaData.put("id", id);
            metaData.put("description", desc);
            metaData.put("name", name);
            metaData.put("creationDate", creationDate);

            return metaData;
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }
}
