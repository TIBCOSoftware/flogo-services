package flowinstance

import (
	"encoding/json"
	"fmt"
	"github.com/julienschmidt/httprouter"
	"github.com/op/go-logging"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
	"errors"
	"strconv"
	"github.com/TIBCOSoftware/flogo-services/flow-state/persistence"
)

var log = logging.MustGetLogger("instance")
var FLOW_NAMESPACE = "flow:"

var STEP_NAMESPACE = "step:"

var STEPS_NAMESPACE = "steps:"

var STEP_FLOWS_KEY = "stepFlows"


func GetSnapshotStatus(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Debug("Get flow " + flowID + " status")
	metadata, err := FlowStatus(flowID)
	if err != nil {
		HandleInternalError(response, errors.New("Get flow " + flowID + " status error"))
		log.Errorf("Get flow " + flowID + " status error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(metadata)
		log.Info(string(jsonFlow))
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListFlowSteps(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("List flow " + flowID + " status")
	results, err := ListSteps(flowID, false)
	if err != nil {
		HandleInternalError(response, errors.New("List flow " + flowID + " steps error"))
		log.Errorf("List flow " + flowID + " steps error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		jsonFlow, _ := json.Marshal(results)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListSteps(flowID string, withStatus bool) (map[string]interface{}, error) {
	var steps []interface{}
	var tasks []interface{}
	taskMetadata := map[string]interface{}{}
	flowMetadata := map[string]interface{}{}
	result := map[string]interface{}{}
	step := map[string]interface{}{}

	changesCommand := persistence.ReditClient.LRange(STEPS_NAMESPACE + flowID, 0, -1)
	changes, err := changesCommand.Result()
	if err != nil {
		return nil, err
	}

	if len(changes) == 0 {
		log.Debug("Steps for instance: " + flowID + " not found")
	}

	for _, change := range changes {
		changesJsonCommand := persistence.ReditClient.HGetAll(change)
		changesJson, err := changesJsonCommand.Result()
		if err != nil {
			return nil, err
		}

		if changesJson == nil {
			log.Debug("Step: " + change + " not found")
		}

		log.Debug("Change json: %+v", changesJson)

		stepId, ok := changesJson["id"]
		if !ok {
			log.Debug("ID not found in step: " + change)
		}

		stepData := changesJson["stepData"]
		if stepData == "" {
			log.Debug("No step data found")
		}

		stepDataObj := &StepData{}
		steperr := json.Unmarshal([]byte(stepData), stepDataObj)
		if steperr != nil {
			return nil, steperr
		}

		tdchanges := stepDataObj.TdChanges
		snapshotDataObj := SnapshotData{}
		for _, tdchange := range tdchanges {
			taskId := tdchange.ID
			jsonSnapshot := persistence.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData").Val()
			if jsonSnapshot != "" {
				steperr := json.Unmarshal([]byte(jsonSnapshot), &snapshotDataObj)
				if steperr != nil {
					return nil, err
				}
				rootTaskEnv := snapshotDataObj.RootTaskEnv
				taskDatas := rootTaskEnv.TaskDatas
				if taskDatas != nil {
					for _, taskData := range taskDatas {
						log.Debug("taskData :", taskData.TaskId, " taskId: ", taskId)
						if taskId == taskData.TaskId {
							attrs := taskData.Attrs
							log.Debug("Found task match!!!")
							log.Debug("taskID: ", taskData.TaskId)
							log.Debug("stepId: ", stepId)
							taskMetadata["taskId"] = taskData.TaskId
							taskMetadata["attributes"] = attrs
							if attrs != nil {
								attributes := attrs.([]interface{})
								if len(attributes) > 0 {
									tasks = append(tasks, taskMetadata)
								}
							}

							taskMetadata = make(map[string]interface{})
						}
					}
				} else {
					log.Debug("Task datas not found for snapshot: " + SNAPSHOT_NAMESPACE + flowID + ":", stepId)
				}
			} else {
				log.Debug("Snapshot for instance step: " + flowID + ":", stepId, " not found")
			}
		}

		if snapshotDataObj.ID == "" {
			jsonSnapshot := persistence.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData").Val()
			if jsonSnapshot != "" {
				steperr := json.Unmarshal([]byte(jsonSnapshot), &snapshotDataObj)
				if steperr != nil {
					return nil, err
				}
			}
		}

		if snapshotDataObj.ID != "" {
			var tmpTaskId int64
			var stepTaskId int64
			wqChanges := stepDataObj.WqChanges
			if wqChanges != nil && len(wqChanges) > 0 {
				for _, wqchange := range wqChanges {
					tmpTaskId = wqchange.Wtem.TaskId
					chgType := wqchange.ChgType

					if chgType == 3 && tmpTaskId != 1 {
						stepTaskId = tmpTaskId
						break
					}
				}

				if stepTaskId == 0 {
					stepTaskId = 1
				}
			}

			flowAttrs := snapshotDataObj.Attrs
			if flowAttrs != nil {
				flowMetadata["state"] = snapshotDataObj.State
				flowMetadata["status"] = snapshotDataObj.Status
				flowMetadata["attributes"] = flowAttrs
				step["flow"] = flowMetadata
			}
			step["taskId"] = stepTaskId
			step["id"] = stepId

			flowMetadata = make(map[string]interface{})
			step["tasks"] = tasks
			steps = append(steps, step)
			tasks = tasks[0:0]
			step = make(map[string]interface{})
			snapshotDataObj = (SnapshotData{})
		}
	}

	if withStatus {
		metadata, err := FlowStatus(flowID)
		if err != nil {
			return nil, err
		}
		result["status"] = metadata["status"]

	}

	result["steps"] = steps

	return result, nil
}

func GetSnapshotStep(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowId := params.ByName("flowID")
	stepId := params.ByName("id")

	log.Info("get snapshot step,  flow:" + flowId + " Step id:" + stepId)

	resultCommand := persistence.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowId + ":" + stepId, "snapshotData")
	vals, err := resultCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get flow " + flowId + " and step " + stepId + " snapshot data  error"))
		log.Errorf("Get flow " + flowId + " and step " + stepId + " snapshot data  error: %v", err)
		return
	} else {
		log.Info("===========", vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%s", vals)
	}
}

func GetInstanceSteps(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowId := params.ByName("flowID")
	log.Info("Get instance steps, flow " + flowId)

	persistence.ReditClient.SRem("flows", flowId)
	sliceCommand := persistence.ReditClient.Del("flows:" + flowId)

	vals, err := sliceCommand.Result()
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", vals)
	}
}

func FlowMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("Get snapshot metadata, flow id: " + flowID)

	metadata, err := persistence.GetSnapshotMetdata(flowID)
	if err != nil {
		HandleInternalError(response, errors.New("Get flow " + flowID + " snapshot metadata error"))
		log.Errorf("Get flow " + flowID + " snapshot metadata error: %v", err)
		return
	}
	response.Header().Set("Content-Type", "application/json")
	jsonFlow, _ := json.Marshal(metadata)
	fmt.Fprintf(response, "%s", jsonFlow)

}

func ListSnapshots(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	resultCommand := persistence.ReditClient.SInter(SNAPSHOTS_FLOWS_KEY)
	vals, err := resultCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get snapshot keys error"))
		log.Errorf("Get snapshot keys error error: %v", err)
		return
	} else {
		results := make([]map[string]string, len(vals))

		for index, name := range vals {
			metadata, err := persistence.GetSnapshotMetdata(name)
			if err != nil {
				HandleInternalError(response, errors.New("Get flow " + name + " snapshot metadata error"))
				log.Errorf("Get flow " + name + " snapshot metadata error: %v", err)
				return
			}
			results[index] = metadata
		}
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(results)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListInstanceStatus(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	command := persistence.ReditClient.Keys("flow:*")
	vals, err := command.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get flow keys error"))
		log.Errorf("Get flow keys error: %v", err)
		return
	} else {

		results := make([]map[string]string, len(vals))

		for index, element := range vals {
			result := persistence.ReditClient.HGetAll(element)
			allResult, getallErr := result.Result()
			if getallErr != nil {
				HandleInternalError(response, errors.New("Get flow key data error"))
				log.Errorf("Get flow key data error: %v", getallErr)
				return
			} else {
				allResult["id"] = strings.Replace(element, "flow:", "", 1)
				results[index] = allResult
			}
		}

		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(results)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func PostChange(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {

	content, err := ioutil.ReadAll(request.Body)
	if err != nil {
		HandleInternalError(response, errors.New("Read body error"))
		log.Errorf("Read body error: %v", err)
		return
	}

	contentMap := StepInfo{}
	jsonerr := json.Unmarshal(content, &contentMap)
	if jsonerr != nil {
		HandleInternalError(response, errors.New("Unmarshal step post body error"))
		log.Debugf("Step content: ", string(content))
		log.Errorf("Unmarshal step post body error %v", jsonerr)
		return
	}

	change := make(map[string]string)
	flowID := contentMap.FlowID
	id := strconv.FormatInt(contentMap.ID, 10)
	change["flowID"] = flowID
	change["id"] = id
	stepData, stepDataErr := json.Marshal(contentMap.StepData)
	if (stepDataErr != nil) {
		HandleInternalError(response, errors.New("Marshal step data error while save steps"))
		log.Errorf("Marshal step data error while save steps: %v", stepDataErr)
	} else {
		change["stepData"] = string(stepData)
	}
	change["state"] = strconv.FormatInt(contentMap.State, 10)
	change["status"] = strconv.FormatInt(contentMap.Status, 10)
	change["date"] = time.Now().String()

	key := STEP_NAMESPACE + flowID + ":" + id

	//TODO error handling
	statusMap := make(map[string]string)
	statusMap["status"] = strconv.FormatInt(contentMap.Status, 10)
	persistence.ReditClient.HMSet(FLOW_NAMESPACE + flowID, statusMap)
	persistence.ReditClient.SAdd(STEP_FLOWS_KEY, flowID + ":" + id)
	persistence.ReditClient.HMSet(key, change)
	pushCommand := persistence.ReditClient.RPush(STEPS_NAMESPACE + flowID, key)
	vals, err := pushCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Save changes error"))
		log.Errorf("Save changes error: %v", err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", vals)
	}
}

func POSTSnapshot(response http.ResponseWriter, request *http.Request, params httprouter.Params) {

	content, err := ioutil.ReadAll(request.Body)
	if err != nil {
		HandleInternalError(response, errors.New("Read body error"))
		log.Errorf("Read body error: %v", err)
		return
	}
	log.Debugf("Snapshot content: ", string(content))
	contentMap := Snapshot{}
	jsonerr := json.Unmarshal(content, &contentMap)
	if jsonerr != nil {
		HandleInternalError(response, errors.New("Unmarshal snapshot post body error"))
		log.Debugf("Snapshot content: ", string(content))
		log.Errorf("Unmarshal snapshot post body error %v", jsonerr)
		return
	}

	snapshot := make(map[string]string)
	snapshot["flowID"] = contentMap.FlowID
	snapshot["id"] = strconv.FormatInt(contentMap.ID, 10)
	v, snapErr := json.Marshal(contentMap.SnapshotData)
	if (snapErr != nil) {
		HandleInternalError(response, errors.New("Marshal step data error while save steps"))
		log.Errorf("Marshal step data error while save steps: %v", snapErr)
	} else {
		snapshot["snapshotData"] = string(v)
	}
	snapshot["state"] = strconv.FormatInt(contentMap.State, 10)
	snapshot["status"] = strconv.FormatInt(contentMap.Status, 10)
	snapshot["date"] = time.Now().String()

	key := SNAPSHOT_NAMESPACE + contentMap.FlowID + ":" + strconv.FormatInt(contentMap.ID, 10)

	//TODO error handling
	persistence.ReditClient.SAdd(SNAPSHOTS_FLOWS_KEY, contentMap.FlowID + ":" + strconv.FormatInt(contentMap.ID, 10))
	persistence.ReditClient.HMSet(key, snapshot)
	pushCommand := persistence.ReditClient.RPush(SNAPSHOTS_NAMESPACE + contentMap.FlowID, key)
	vals, err := pushCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Save snapshot changes error"))
		log.Errorf("Save snapshot changes error: %v", err)
		return
	} else {
		log.Info("----------", vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", vals)
	}
}

func DeleteFLow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Debug("Delete flow " + flowID)

	//retrive snapshot names
	snapshotsCommand := persistence.ReditClient.LRange(SNAPSHOTS_NAMESPACE + flowID, 0, -1)
	snapshots, err := snapshotsCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get snapshot names error"))
		log.Errorf("Get snapshot names error: %v", err)
		return
	} else {

		for _, snapshot := range snapshots {
			keysCommand := persistence.ReditClient.HKeys(snapshot)
			keys, err := keysCommand.Result()
			if err != nil {
				HandleInternalError(response, errors.New("Get snapshot keys error"))
				log.Errorf("Get snapshot keys error: %v", err)
				return
			} else {
				for _, key := range keys {
					response := persistence.ReditClient.HDel(snapshot, key)
					log.Debug("snapshot flow key: " + flowID + " response: ", response.Val())
				}
			}
		}
	}

	remSnapShotRespComand := persistence.ReditClient.Del(SNAPSHOTS_NAMESPACE + flowID)
	log.Debug("snapshot: " + flowID + " response: ", remSnapShotRespComand.Val())
	remSnapShotResp, err := remSnapShotRespComand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Delete snapshot error"))
		log.Errorf("Get snapshot error: %v", err)
		return
	}
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusOK)
	fmt.Fprintf(response, "%d", remSnapShotResp)
}
