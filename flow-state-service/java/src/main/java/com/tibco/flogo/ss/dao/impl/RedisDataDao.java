package com.tibco.flogo.ss.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibco.flogo.ss.dao.DataDao;
import com.tibco.flogo.ss.obj.StepInfo;
import com.tibco.flogo.ss.obj.SnapshotInfo;
import com.tibco.flogo.ss.service.PropertyClient;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by mregiste on 2/10/2016.
 */
public class RedisDataDao implements DataDao
{
    private AtomicLong m_atomicLong = new AtomicLong();

    private static final Logger LOG = Logger.getLogger(RedisDataDao.class);
    private static JedisPool m_pool;

    private static final String INSTANCE_NAMESPACE      = "instance:";
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
    public Map<String, String> getSnapshot(String flowID, int version)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.hgetAll(SNAPSHOT_NAMESPACE + flowID +":" +version);
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

            Map<String, String> snapshot = new HashMap<String, String>();
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
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            Transaction t = jedis.multi();
            Response<Long> remProcListResp = t.srem(SNAPSHOTS_FLOWS_KEY, flowID);
            Response<Long> remProcResp = t.del(SNAPSHOT_NAMESPACE, flowID);
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
    public List<Map<String, String>> getInstancesMetadata()
    {
        Jedis jedis = null;
        try
        {
            List<Map<String, String>> results = new ArrayList<Map<String, String>>();
            jedis = m_pool.getResource();
            Set<String> names=jedis.keys(INSTANCE_NAMESPACE +"*");

            Iterator<String> it = names.iterator();
            while (it.hasNext())
            {
                String s = it.next();
                Map<String, String> instance = jedis.hgetAll(s);
                instance.put("id", s.replaceFirst(INSTANCE_NAMESPACE, ""));
                results.add(instance);
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
    public Map<String, String> getInstanceStatus(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(INSTANCE_NAMESPACE + flowID, "status");

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


    @Override
    public Map<String, Object> listSteps(String flowID, boolean withStatus)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            Integer taskId = null;
            ArrayList attrs = null;
            LinkedHashMap changesMap;
            LinkedHashMap snapshotMap = null;
            // get list of step entries
            List<String> changes = jedis.lrange(STEPS_NAMESPACE + flowID, 0, -1);
            if(changes == null || changes.isEmpty())
                LOG.debug("Steps for instance: " +flowID +" not found");
            Map<String, Object> results = new HashMap<String, Object>();
            List steps = new ArrayList<Object>();
            List tasks = new ArrayList<Object>();
            Map<String, Object> taskMetadata = new HashMap<String, Object>();
            Map<String, Object> flowMetatdata = new HashMap<String, Object>();
            Map<String, Object> step = new HashMap<String, Object>();
            for (String change : changes)
            {
                Map<String, String> changesJson = jedis.hgetAll(change);
                if(changesJson == null)
                    LOG.debug("Step: " +change +" not found");
                String stepId = changesJson.get("id");
                if(stepId == null)
                    LOG.debug("ID not found in step: " +change);
                ObjectMapper mapper = new ObjectMapper();
                try
                {
                    changesMap =  mapper.readValue(changesJson.get("stepData"), LinkedHashMap.class);
                    if(changesMap == null)
                        LOG.debug("Object mapper failed to convert: " +change);
                    ArrayList<LinkedHashMap> tdChanges = (ArrayList) changesMap.get("tdChanges");
                    if(tdChanges != null) {
                        for (LinkedHashMap tdChange : tdChanges) {
                            taskId = (Integer) tdChange.get("ID");
                            String jsonSnapshot = jedis.hget(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData");
                            if (jsonSnapshot != null) {
                                ObjectMapper snapshotMapper = new ObjectMapper();
                                try {
                                    snapshotMap = snapshotMapper.readValue(jsonSnapshot, LinkedHashMap.class);
                                    if(snapshotMap == null)
                                        LOG.debug("Object mapper failed to convert snapshot: " +SNAPSHOT_NAMESPACE + flowID + ":" + stepId);
                                    LinkedHashMap rootTaskEnv = (LinkedHashMap) snapshotMap.get("rootTaskEnv");
                                    ArrayList<LinkedHashMap> taskDatas = (ArrayList) rootTaskEnv.get("taskDatas");
                                    if(taskDatas != null) {
                                        for (LinkedHashMap taskData : taskDatas) {
                                            LOG.debug("taskData :" + taskData.get("taskID") + " taskId: " + taskId);
                                            if (taskData.get("taskID") == taskId) {
                                                attrs = (ArrayList) taskData.get("attrs");
                                                LOG.debug("Found task match!!!");
                                                LOG.debug("taskID: " + taskData.get("taskID"));
                                                LOG.debug("stepId: " + stepId);
//                                            taskMetadata.put("id", stepId);
                                                taskMetadata.put("taskId", taskData.get("taskID"));
                                                taskMetadata.put("attributes", attrs);
                                                if (attrs != null && !attrs.isEmpty())
                                                    tasks.add(taskMetadata);
                                                // clear
                                                taskMetadata = new HashMap<String, Object>();
                                            }
                                        }
                                    }
                                    else
                                        LOG.debug("Task datas not found for snapshot: " +SNAPSHOT_NAMESPACE + flowID + ":" + stepId);
                                } catch (Exception e) {
                                    LOG.debug("Exception in listSteps(snapshot)", e);
                                }
                            }
                            else
                                LOG.debug("Snapshot for instance step: " +flowID + ":" + stepId +" not found");
                        }
                    }

                    // no tdChanges found
                    if(snapshotMap == null) {
                        String jsonSnapshot = jedis.hget(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData");
                        if(jsonSnapshot != null) {
                            ObjectMapper snapshotMapper = new ObjectMapper();
                            snapshotMap = snapshotMapper.readValue(jsonSnapshot, LinkedHashMap.class);
                        }
                    }

                    if(snapshotMap != null) {
                        Integer tmpTaskId = null;
                        Integer stepTaskId = null;
                        ArrayList<LinkedHashMap> wqChanges = (ArrayList) changesMap.get("wqChanges");
                        if(wqChanges != null) {
                            for (LinkedHashMap wqChange : wqChanges) {
                                tmpTaskId = (Integer) ((Map)wqChange.get("WorkItem")).get("taskID");
                                Integer chgType = (Integer) wqChange.get("ChgType");
                                if (chgType == 3 && tmpTaskId != 1) {
                                    stepTaskId = tmpTaskId;
                                    break;
                                }
                            }
                            if(stepTaskId == null)
                                stepTaskId = 1;
                        }
                        ArrayList flowAttrs = (ArrayList) snapshotMap.get("attrs");
                        if(flowAttrs != null) {
                            flowMetatdata.put("state", snapshotMap.get("state"));
                            flowMetatdata.put("status", snapshotMap.get("status"));
                            flowMetatdata.put("attributes", flowAttrs);
                            step.put("flow", flowMetatdata);
                        }
                        step.put("taskId", stepTaskId);
                        step.put("id", stepId);

                        // clear
                        flowMetatdata = new HashMap<String, Object>();
                        step.put("tasks", tasks);
                        // clear
                        tasks = new ArrayList<Object>();
                        steps.add(step);
                        // clear
                        step = new HashMap<String, Object>();
                        snapshotMap = null;
                    }
                }
                catch (Exception e)
                {
                    LOG.debug("Exception in listSteps", e);
                }
            }

            if(withStatus)
            {
                Map<String, String> flowStatus = ConfigDaoImpl.getInstance().getInstanceStatus(flowID);
                results.put("status", flowStatus.get("status"));
            }

            results.put("steps", steps);

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
    public List<Map<String, String>> listSteps()
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            Set<String> all = jedis.sinter(STEP_FLOWS_KEY);
            List<Map<String, String>> results = new ArrayList<>(all.size());
            for (String id : all)
            {
                results.add(getStepMetadata(id));
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

            Map<String, String> change = new HashMap<String, String>();
            change.put(StepInfo.FLOW_ID, flowID);
            change.put(StepInfo.ID, String.valueOf(id));
            change.put(StepInfo.STEP_DATA, stepString);
            change.put(StepInfo.STATE, String.valueOf(state));
            change.put(StepInfo.STATUS, String.valueOf(status));
            change.put(StepInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = STEP_NAMESPACE + flowID + ":" + id;

            // add flow status
            t.hmset(INSTANCE_NAMESPACE + flowID, new HashMap<String, String>(){{put("status", String.valueOf(status));}});

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

    @Override
    public long saveStep(StepInfo stepInfo)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            Map<String, String> change = new HashMap<String, String>();
            change.put(StepInfo.ID, stepInfo.getId());
            change.put(StepInfo.STEP_DATA, stepInfo.getStepData());
            change.put(StepInfo.STATE, stepInfo.getState());
            change.put(StepInfo.STATUS, stepInfo.getStatus());
            change.put(StepInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = STEP_NAMESPACE + stepInfo.getFlowId() + ":" + stepInfo.getId();

            t.sadd(STEP_FLOWS_KEY, stepInfo.getFlowId()); //add flow to list
            t.hmset(key, change); //add individual stepData
            Response<Long> addChangeListResp = t.rpush(STEP_NAMESPACE + stepInfo.getFlowId(), key); //add stepData to stepData list
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

    @Override
    public long removeStep(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            Set<String> keys = jedis.keys(STEP_NAMESPACE + flowID + ":");
            for (String key : keys) {
                jedis.del(key);
            }

            Transaction t = jedis.multi();


            for (String key : keys) {
                t.del(key);
            }

            t.del(STEP_NAMESPACE + flowID);
            Response<Long> remChangeResp = t.srem(STEP_FLOWS_KEY, flowID);

            t.exec();
            return (remChangeResp == null) ? 0 : remChangeResp.get();
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
    public Map<String, String> getStepMetadata(String flowID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            List<String> changes = jedis.lrange(STEP_NAMESPACE + flowID,-1L,-1L);


            if (!changes.isEmpty())
            {
                String change = changes.get(0);
                String changeId = jedis.hget(change, StepInfo.ID);
                String state = jedis.hget(change, StepInfo.STATE);
                String status = jedis.hget(change, StepInfo.STATUS);
                String creationDate = jedis.hget(change, StepInfo.DATE);

                Map<String, String> metaData = new HashMap<String, String>();
                metaData.put(StepInfo.ID, changeId);
                metaData.put(StepInfo.STATE, state);
                metaData.put(StepInfo.STATUS, status);
                metaData.put(StepInfo.DATE, creationDate);
                return metaData;
            }
            else
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
    public String getSnapshotStep(String id, String stepId) {
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
}
