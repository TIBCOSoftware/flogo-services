package com.tibco.flogo.ss.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.DataDao;
import com.tibco.flogo.ss.obj.SnapshotData;
import com.tibco.flogo.ss.obj.StepData;
import com.tibco.flogo.ss.obj.StepInfo;
import com.tibco.flogo.ss.obj.SnapshotInfo;
import com.tibco.flogo.ss.service.PropertyClient;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;


/**
 * Created by mregiste on 2/10/2016.
 */
public class RedisDataDao implements DataDao
{
    private static       RedisDataDao theInstance = new RedisDataDao();
    private static final Logger LOG = Logger.getLogger(RedisDataDao.class);
    private static JedisPool m_pool;

    private static final String FLOW_NAMESPACE      = "flow:";
    private static final String STEP_NAMESPACE        = "step:";
    private static final String STEPS_NAMESPACE       = "steps:";
    private static final String STEP_FLOWS_KEY    = "stepFlows";
    private static final String SNAPSHOT_NAMESPACE      = "snapshot:";
    private static final String SNAPSHOTS_NAMESPACE      = "snapshots:";
    private static final String SNAPSHOTS_FLOWS_KEY = "snapshotFlows";

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
    public Map<String, String> getSnapshot(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.hgetAll(SNAPSHOT_NAMESPACE + id);
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
    public long saveSnapshot(String flowID, Integer id, Integer status, Integer state, Object snapshotObject)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String snapshotString;
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                snapshotString =  mapper.writeValueAsString(snapshotObject);
            }
            catch (IOException e)
            {
                return 0;
            }

            Map<String, String> snapshot = new HashMap<>();
            snapshot.put(SnapshotInfo.FLOW_ID, String.valueOf(flowID));
            snapshot.put(SnapshotInfo.ID, String.valueOf(id));
            snapshot.put(SnapshotInfo.STATUS, String.valueOf(status));
            snapshot.put(SnapshotInfo.STATE, String.valueOf(state));
            snapshot.put(SnapshotInfo.SNAPSHOT_DATA, snapshotString);
            snapshot.put(SnapshotInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = SNAPSHOT_NAMESPACE + flowID + ":" + id;
            t.sadd(SNAPSHOTS_FLOWS_KEY, flowID + ":" +id); //add snapshot to list
            t.hmset(key, snapshot); //add individual snapshot
            Response<Long> addSnapshotListResp = t.rpush(SNAPSHOTS_NAMESPACE + flowID, key); //add stepData to stepData list

            t.exec();
            return addSnapshotListResp.get();
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
    public long removeSnapshot(String flowID)
    {
        // todo needs to be in one transaction
        Jedis jedis = null;
        try
        {
            long remSnapShotResp;
            jedis = m_pool.getResource();

            // retrieve snapshot names
            List<String> snapshots = jedis.lrange(SNAPSHOTS_NAMESPACE + flowID, 0, -1);

            for (String snapshot : snapshots) {
                // returns all keys of the snapshot: status, flowID, state, id, data and snapshotData
                Set<String> keys = jedis.hkeys(snapshot);
                for (String key : keys) {
                    long remSnapShotHash = jedis.hdel(snapshot, key);
                    LOG.debug("snapshot hash: " +key +" response: " +remSnapShotHash);
                }

                long remSnapShotFlow = jedis.srem(SNAPSHOTS_FLOWS_KEY, snapshot.replaceFirst(SNAPSHOT_NAMESPACE, ""));
                LOG.debug("snapshot flow key: " +flowID +" response: " +remSnapShotFlow);
            }

            remSnapShotResp = jedis.del(SNAPSHOTS_NAMESPACE + flowID);
            LOG.debug("snapshot: " +flowID +" response: " +remSnapShotResp);

            return remSnapShotResp;
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
    public long removeFlow(String flowID)
    {
        Jedis jedis = null;
        try
        {
            long remFlowHash = 0;
            jedis = m_pool.getResource();

            String flowKey = FLOW_NAMESPACE + flowID;
            Set<String> keys = jedis.hkeys(flowKey);
            for (String key : keys) {
                remFlowHash = jedis.hdel(flowKey, key);
                LOG.debug("flow hash: " +key +" response: " +remFlowHash);
            }
            LOG.debug("flow: " +flowID +" response: " +remFlowHash);

            return remFlowHash;
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
    public long removeSteps(String flowID)
    {
        // todo needs to be in one transaction
        Jedis jedis = null;
        try
        {
            long remStepResp;
            jedis = m_pool.getResource();

            // retrieve step names
            List<String> steps = jedis.lrange(STEPS_NAMESPACE + flowID, 0, -1);

            for (String step : steps) {
                Set<String> keys = jedis.hkeys(step);
                for (String key : keys) {
                    long remSnapShotHash = jedis.hdel(step, key);
                    LOG.debug("step hash: " +key +" response: " +remSnapShotHash);
                }

                long remSnapShotFlow = jedis.srem(STEP_FLOWS_KEY, step.replaceFirst(STEP_NAMESPACE, ""));
                LOG.debug("step flow key: " +flowID +" response: " +remSnapShotFlow);
            }

            remStepResp = jedis.del(STEPS_NAMESPACE + flowID);
            LOG.debug("step: " +flowID +" response: " +remStepResp);

            return remStepResp;
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
    public String drop()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.flushAll();
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
            return jedis.keys(FLOW_NAMESPACE +"*");
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    /**
     * List all steps for every flow
     * @return - Set of <flowid>:<sid>
     */
    @Override
    public Set<String> listAllFlowStepIds()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.sinter(STEP_FLOWS_KEY);
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    /**
     * List all steps for a flow
     * @param flowId
     * @return - Set of <flowid>:<sid>
     */
    @Override
    public List<String> listAllFlowStepIds(String flowId)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.lrange(STEPS_NAMESPACE + flowId, 0, -1);
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    /**
     * List all snapshots for a flow
     * @param flowId
     * @return - Set of <flowid>:<sid>
     */
    @Override
    public List<String> listAllFlowSnapshotIds(String flowId)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.lrange(SNAPSHOTS_NAMESPACE + flowId, 0, -1);
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
    public Set<String> listSnapshots()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.sinter(SNAPSHOTS_FLOWS_KEY);
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
    public List<Map<String, String>> getFlowsMetadata()
    {
        Jedis jedis = null;
        try
        {
            List<Map<String, String>> results = new ArrayList<>();
            jedis = m_pool.getResource();
            Set<String> names=jedis.keys(FLOW_NAMESPACE +"*");

            for (String s : names) {
                Map<String, String> flow = jedis.hgetAll(s);
                flow.put("id", s.replaceFirst(FLOW_NAMESPACE, ""));
                results.add(flow);
            }
            return results;
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
    public Map<String, String> getSnapshotMetadata(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(SNAPSHOT_NAMESPACE + flowID, SnapshotInfo.STATUS);
            String state = jedis.hget(SNAPSHOT_NAMESPACE + flowID, SnapshotInfo.STATE);
            String date = jedis.hget(SNAPSHOT_NAMESPACE + flowID, SnapshotInfo.DATE);
            String id = jedis.hget(SNAPSHOT_NAMESPACE + flowID, SnapshotInfo.ID);

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.FLOW_ID, flowID);
            metaData.put(SnapshotInfo.ID, id);
            metaData.put(SnapshotInfo.STATUS, status);
            metaData.put(SnapshotInfo.STATE, state);
            metaData.put(SnapshotInfo.DATE, date);

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

    @Override
    public Map<String, String> getFlowStatus(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(FLOW_NAMESPACE + flowID, "status");

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.ID, flowID);
            metaData.put(SnapshotInfo.STATUS, status);

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

    @Override
    public Map<String, String> getSnapshotStatus(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(SNAPSHOT_NAMESPACE + flowID, SnapshotInfo.STATUS);

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.FLOW_ID, flowID);
            metaData.put(SnapshotInfo.STATUS, status);

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

    /**
     *
     * @param id - <flowid>:<sid>
     * @return
     */
    @Override
    public Map<String, String> listStepData(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            // steps step:<flowid>:<sid>
            if(id.startsWith(STEP_NAMESPACE))
                return jedis.hgetAll(id);
            else
                return jedis.hgetAll(STEP_NAMESPACE + id);
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    public List<Map<String, String>> listFlowStepData(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            if(flowID.contains(":"))
            {
                throw new IllegalArgumentException("Invalid flow id format: " +flowID);
            }

            List<String> steps = jedis.lrange(STEPS_NAMESPACE + flowID, 0, -1);
            List<Map<String, String>> results = new ArrayList<>(steps.size());
            for (String id : steps)
            {
                results.add(getStep(id));
            }

            return results;
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
    public List<Map<String, String>> listAllStepData()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            Set<String> all = jedis.sinter(STEP_FLOWS_KEY);
            List<Map<String, String>> results = new ArrayList<>(all.size());
            for (String id : all)
            {
                results.add(getStep(id));
            }

            return results;
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
    public long saveStep(String flowID, Integer id, Integer state, Integer status, Object stepInfo)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String stepString;
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                stepString =  mapper.writeValueAsString(stepInfo);
            }
            catch (IOException e)
            {
                return 0;
            }

            Map<String, String> change = new HashMap<>();
            change.put(StepInfo.FLOW_ID, flowID);
            change.put(StepInfo.ID, String.valueOf(id));
            change.put(StepInfo.STEP_DATA, stepString);
            change.put(StepInfo.STATE, String.valueOf(state));
            change.put(StepInfo.STATUS, String.valueOf(status));
            change.put(StepInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = STEP_NAMESPACE + flowID + ":" + id;

            // add flow status
            t.hmset(FLOW_NAMESPACE + flowID, new HashMap<String, String>(){{put("status", String.valueOf(status));}});

            t.sadd(STEP_FLOWS_KEY, flowID + ":" +id); //add flow to list
            t.hmset(key, change); //add individual stepData
            Response<Long> addChangeListResp = t.rpush(STEPS_NAMESPACE + flowID, key); //add stepData to stepData list
            t.exec();
            return addChangeListResp.get();
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    /**
     *
     * @param id - either "step:<flowid>:<stepid></>" or "<flowid>:<stepid>'
     * @return
     */
    public Map<String, String> getStep(String id)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            if(id.startsWith(STEP_NAMESPACE))
                return jedis.hgetAll(id);
            else
                return jedis.hgetAll(STEP_NAMESPACE + id);
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
    public List<StepInfo> getStepInfo(String flowID)
    {
        ArrayList<StepInfo> stepInfoArray = new ArrayList<>();
        StepInfo stepInfo = null;
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            List<String> steps = listAllFlowStepIds(flowID);
            if(steps == null || steps.isEmpty())
                LOG.debug("Steps for flow: " +flowID +" not found");
            if (steps != null) {
                for (String step : steps)
                {
                    Map<String, String> changesJson = jedis.hgetAll(step);
                    if(changesJson == null)
                        LOG.debug("Step: " +step +" not found");
                    else
                    {
                        try
                        {
                            ObjectMapper mapper = new ObjectMapper();
                            stepInfo = mapper.convertValue(changesJson, StepInfo.class);

                            StepData stepData = mapper.readValue(changesJson.get(StepInfo.STEP_DATA), StepData.class);
                            stepInfo.setStepData(stepData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        stepInfoArray.add(stepInfo);
                    }
                }
            }
            return stepInfoArray;
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
    public SnapshotInfo getSnapshotInfo(String flowID, Integer snapshotId)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String key = SNAPSHOT_NAMESPACE + flowID +":" +snapshotId;
            Map<String, String> snapshotJson = jedis.hgetAll(key);
            if(snapshotJson == null || snapshotJson.isEmpty())
                LOG.debug("Snapshot: " + key + " not found");
            else
            {
                try
                {
                    ObjectMapper mapper = new ObjectMapper();
                    SnapshotInfo snapshotInfo = mapper.convertValue(snapshotJson, SnapshotInfo.class);

                    SnapshotData snapshotData = mapper.readValue(snapshotJson.get(SnapshotInfo.SNAPSHOT_DATA), SnapshotData.class);
                    snapshotInfo.setSnapshot(snapshotData);

                    return snapshotInfo;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
    public List<SnapshotInfo> getSnapshotInfo(String flowID)
    {
        ArrayList<SnapshotInfo> snapshotInfoArray = new ArrayList<>();
        SnapshotInfo snapshotInfo = null;
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            List<String> snapshots = listAllFlowSnapshotIds(flowID);
            if(snapshots == null || snapshots.isEmpty())
                LOG.debug("Snapshots for flow: " +flowID +" not found");
            if (snapshots != null) {
                for (String snapshot : snapshots)
                {
                    Map<String, String> snapshotJson = jedis.hgetAll(snapshot);
                    if(snapshotJson == null)
                        LOG.debug("Snapshot: " +snapshot +" not found");
                    else
                    {
                        try
                        {
                            ObjectMapper mapper = new ObjectMapper();
                            snapshotInfo = mapper.convertValue(snapshotJson, SnapshotInfo.class);

                            SnapshotData snapshotData = mapper.readValue(snapshotJson.get(SnapshotInfo.SNAPSHOT_DATA), SnapshotData.class);
                            snapshotInfo.setSnapshot(snapshotData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        snapshotInfoArray.add(snapshotInfo);
                    }
                }
            }
            return snapshotInfoArray;
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
    public String getSnapshot(String id, String stepId) {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.hget(SNAPSHOT_NAMESPACE +id +":" +stepId, "snapshotData");
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }
    }

    // helpers
    private static final String DELETE_SCRIPT_IN_LUA = "local keys = redis.call('keys', '%s')" +
            "  for i,k in ipairs(keys) do" +
            "    local res = redis.call('del', k)" +
            "  end";

    public void deleteKeys(String pattern) {
        Jedis jedis = null;

        try {
            jedis = m_pool.getResource();

            jedis.eval(String.format(DELETE_SCRIPT_IN_LUA, pattern));
        }
        finally
        {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Return the single instance of this Contacts.
     *
     * @return The Contacts instance.
     */
    public static RedisDataDao getInstance()
    {
        return theInstance;
    }
}
