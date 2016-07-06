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
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by mregiste on 2/10/2016.
 */
public class RedisDataDao implements DataDao
{
    private static       RedisDataDao theInstance = new RedisDataDao();
    private static final Logger LOG = Logger.getLogger(RedisDataDao.class);
    private static JedisPool m_pool;

    private static final String INSTANCE_NAMESPACE      = "instance:";
    private static final String STEP_NAMESPACE        = "step:";
    private static final String STEPS_NAMESPACE       = "steps:";
    private static final String STEP_PROCESSES_KEY    = "stepProcesses";
    private static final String SNAPSHOT_NAMESPACE      = "snapshot:";
    private static final String SNAPSHOTS_NAMESPACE      = "snapshots:";
    private static final String SNAPSHOTS_PROCESSES_KEY = "snapshotProcesses";

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
    public Map<String, String> getSnapshot(String processID, int version)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            return jedis.hgetAll(SNAPSHOT_NAMESPACE + processID +":" +version);
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
    public long saveSnapshot(String processID, Integer id, Integer status, Integer state, Object snapshotObject)
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
            snapshot.put(SnapshotInfo.PROCESS_ID, String.valueOf(processID));
            snapshot.put(SnapshotInfo.ID, String.valueOf(id));
            snapshot.put(SnapshotInfo.STATUS, String.valueOf(status));
            snapshot.put(SnapshotInfo.STATE, String.valueOf(state));
            snapshot.put(SnapshotInfo.SNAPSHOT_DATA, snapshotString);
            snapshot.put(SnapshotInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = SNAPSHOT_NAMESPACE + processID + ":" + id;
            t.sadd(SNAPSHOTS_PROCESSES_KEY, processID + ":" +id); //add snapshot to list
            t.hmset(key, snapshot); //add individual snapshot
            Response<Long> addSnapshotListResp = t.rpush(SNAPSHOTS_NAMESPACE + processID, key); //add stepData to stepData list

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
    public long removeSnapshot(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            Transaction t = jedis.multi();
            Response<Long> remProcListResp = t.srem(SNAPSHOTS_PROCESSES_KEY, processID);
            Response<Long> remProcResp = t.del(SNAPSHOT_NAMESPACE, processID);
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
            return jedis.sinter(SNAPSHOTS_PROCESSES_KEY);
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
    public Map<String, String> getSnapshotMetadata(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(SNAPSHOT_NAMESPACE + processID, SnapshotInfo.STATUS);
            String state = jedis.hget(SNAPSHOT_NAMESPACE + processID, SnapshotInfo.STATE);
            String date = jedis.hget(SNAPSHOT_NAMESPACE + processID, SnapshotInfo.DATE);
            String id = jedis.hget(SNAPSHOT_NAMESPACE + processID, SnapshotInfo.ID);

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.PROCESS_ID, processID);
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
    public Map<String, String> getInstanceStatus(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(INSTANCE_NAMESPACE + processID, "status");

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.ID, processID);
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
    public Map<String, String> getSnapshotStatus(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            String status = jedis.hget(SNAPSHOT_NAMESPACE + processID, SnapshotInfo.STATUS);

            Map<String, String> metaData = new HashMap<>();
            metaData.put(SnapshotInfo.PROCESS_ID, processID);
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
    public Map<String, Object> listSteps(String processID, boolean withStatus)
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
            List<String> changes = jedis.lrange(STEPS_NAMESPACE + processID, 0, -1);
            if(changes == null || changes.isEmpty())
                LOG.debug("Steps for instance: " +processID +" not found");
            Map<String, Object> results = new HashMap<String, Object>();
            List steps = new ArrayList<Object>();
            List tasks = new ArrayList<Object>();
            Map<String, Object> taskMetadata = new HashMap<String, Object>();
            Map<String, Object> processMetatdata = new HashMap<>();
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
                            String jsonSnapshot = jedis.hget(SNAPSHOT_NAMESPACE + processID + ":" + stepId, "snapshotData");
                            if (jsonSnapshot != null) {
                                ObjectMapper snapshotMapper = new ObjectMapper();
                                try {
                                    snapshotMap = snapshotMapper.readValue(jsonSnapshot, LinkedHashMap.class);
                                    if(snapshotMap == null)
                                        LOG.debug("Object mapper failed to convert snapshot: " +SNAPSHOT_NAMESPACE + processID + ":" + stepId);
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
                                        LOG.debug("Task datas not found for snapshot: " +SNAPSHOT_NAMESPACE + processID + ":" + stepId);
                                } catch (Exception e) {
                                    LOG.debug("Exception in listSteps(snapshot)", e);
                                }
                            }
                            else
                                LOG.debug("Snapshot for instance step: " +processID + ":" + stepId +" not found");
                        }
                    }

                    // no tdChanges found
                    if(snapshotMap == null) {
                        String jsonSnapshot = jedis.hget(SNAPSHOT_NAMESPACE + processID + ":" + stepId, "snapshotData");
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
                        ArrayList processAttrs = (ArrayList) snapshotMap.get("attrs");
                        if(processAttrs != null) {
                            processMetatdata.put("state", snapshotMap.get("state"));
                            processMetatdata.put("status", snapshotMap.get("status"));
                            processMetatdata.put("attributes", processAttrs);
                            step.put("process", processMetatdata);
                        }
                        step.put("taskId", stepTaskId);
                        step.put("id", stepId);

                        // clear
                        processMetatdata = new HashMap<String, Object>();
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
                Map<String, String> processStatus = ConfigDaoImpl.getInstance().getInstanceStatus(processID);
                results.put("status", processStatus.get("status"));
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

            Set<String> all = jedis.sinter(STEP_PROCESSES_KEY);
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
    public long saveStep(String processID, Integer id, Integer state, Integer status, Object stepInfo)
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
            change.put(StepInfo.PROCESS_ID, processID);
            change.put(StepInfo.ID, String.valueOf(id));
            change.put(StepInfo.STEP_DATA, stepString);
            change.put(StepInfo.STATE, String.valueOf(state));
            change.put(StepInfo.STATUS, String.valueOf(status));
            change.put(StepInfo.DATE, String.valueOf(new Date()));

            Transaction t = jedis.multi();
            String key = STEP_NAMESPACE + processID + ":" + id;

            // add process status
            t.hmset(INSTANCE_NAMESPACE + processID, new HashMap<String, String>(){{put("status", String.valueOf(status));}});

            t.sadd(STEP_PROCESSES_KEY, processID + ":" +id); //add process to list
            t.hmset(key, change); //add individual stepData
            Response<Long> addChangeListResp = t.rpush(STEPS_NAMESPACE + processID, key); //add stepData to stepData list
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
//        Jedis jedis = null;
//        try
//        {
//            jedis = m_pool.getResource();
//
//            Map<String, String> change = new HashMap<String, String>();
//            change.put(StepInfo.ID, stepInfo.getId());
//            change.put(StepInfo.STEP_DATA, stepInfo.getStepData());
//            change.put(StepInfo.STATE, stepInfo.getState());
//            change.put(StepInfo.STATUS, stepInfo.getStatus());
//            change.put(StepInfo.DATE, String.valueOf(new Date()));
//
//            Transaction t = jedis.multi();
//            String key = STEP_NAMESPACE + stepInfo.getProcessId() + ":" + stepInfo.getId();
//
//            t.sadd(STEP_PROCESSES_KEY, stepInfo.getProcessId()); //add process to list
//            t.hmset(key, change); //add individual stepData
//            Response<Long> addChangeListResp = t.rpush(STEP_NAMESPACE + stepInfo.getProcessId(), key); //add stepData to stepData list
//            t.exec();
//            return addChangeListResp.get();
//        }
//        finally
//        {
//            if (jedis != null)
//            {
//                jedis.close();
//            }
//        }
        return 0l;
    }

    @Override
    public long removeStep(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();

            Set<String> keys = jedis.keys(STEP_NAMESPACE + processID + ":");
            for (String key : keys) {
                jedis.del(key);
            }

            Transaction t = jedis.multi();


            for (String key : keys) {
                t.del(key);
            }

            t.del(STEP_NAMESPACE + processID);
            Response<Long> remChangeResp = t.srem(STEP_PROCESSES_KEY, processID);

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
    /**
     * todo: not sure this works
     */
    public Map<String, String> getStepMetadata(String processID)
    {
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();
            List<String> changes = jedis.lrange(STEP_NAMESPACE + processID,-1L,-1L);


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
    public List<StepInfo> getStepInfo(String processID)
    {
        ArrayList<StepInfo> stepInfoArray = new ArrayList<>();
        StepInfo stepInfo = null;
        Jedis jedis = null;
        try
        {
            jedis = m_pool.getResource();


            List<String> steps = jedis.lrange(STEPS_NAMESPACE + processID, 0, -1);
            if(steps == null || steps.isEmpty())
                LOG.debug("Steps for instance: " +processID +" not found");
            Map<String, Object> results = new HashMap<String, Object>();
            for (String step : steps)
            {
                Map<String, String> changesJson = jedis.hgetAll(step);
                if(changesJson == null)
                    LOG.debug("Step: " +step +" not found");
                stepInfo = new StepInfo();

                String changeId = jedis.hget(step, StepInfo.ID);
                stepInfo.setId(changeId);

                String state = jedis.hget(step, StepInfo.STATE);
                stepInfo.setState(state);

                String status = jedis.hget(step, StepInfo.STATUS);
                stepInfo.setStatus(status);

                // todo - mjr add creation date?
//                String creationDate = jedis.hget(change, StepInfo.DATE);
//                stepInfo.set(changeId);

                String stepDataJson = jedis.hget(step, StepInfo.STEP_DATA);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    StepData stepData = mapper.readValue(stepDataJson, StepData.class);
                    stepInfo.setStepData(stepData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stepInfoArray.add(stepInfo);
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
