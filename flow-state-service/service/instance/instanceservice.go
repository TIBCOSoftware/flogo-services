package instance

import (
	"encoding/json"
	"fmt"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/model"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/stateflow"
	"github.com/julienschmidt/httprouter"
	"github.com/op/go-logging"
	"io/ioutil"
	"net/http"
	"strings"
	"time"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/util"
	"github.com/pkg/errors"
)

var log = logging.MustGetLogger("instance")
var FLOW_NAMESPACE = "flow:"

var STEP_NAMESPACE = "step:"

var STEPS_NAMESPACE = "steps:"

var STEP_FLOWS_KEY = "stepFlows"

var SNAPSHOT_NAMESPACE = "snapshot:"

var SNAPSHOTS_NAMESPACE = "snapshots:"

var SNAPSHOTS_FLOWS_KEY = "snapshotFlows"

func GetSnapshotStatus(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Debug("Get flow " + flowID + " status")
	metadata, err := stateflow.FlowStatus(flowID)
	if err != nil {
		util.HandleInternalError(response, errors.New("Get flow " + flowID + " status error"))
		log.Errorf("Get flow " + flowID + " status error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(metadata)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListFlowSteps(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("List flow " + flowID + " status")
	results, err := ListSteps(flowID, false)
	if err != nil {
		util.HandleInternalError(response, errors.New("List flow " + flowID + " steps error"))
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

	changesCommand := service.ReditClient.LRange(STEPS_NAMESPACE + flowID, 0, -1)
	changes, err := changesCommand.Result()
	if err != nil {
		return nil, err
	}

	if len(changes) == 0 {
		log.Debug("Steps for instance: " + flowID + " not found")
	}

	for _, change := range changes {
		changesJsonCommand := service.ReditClient.HGetAll(change)
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

		stepDataObj := &model.StepData{}
		steperr := json.Unmarshal([]byte(stepData), stepDataObj)
		if steperr != nil {
			return nil, steperr
		}

		tdchanges := stepDataObj.TdChanges
		snapshotDataObj := model.SnapshotData{}
		for _, tdchange := range tdchanges {
			taskId := tdchange.ID
			jsonSnapshot := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData").Val()
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
			jsonSnapshot := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID + ":" + stepId, "snapshotData").Val()
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
			snapshotDataObj = (model.SnapshotData{})
		}
	}

	if withStatus {
		metadata, err := stateflow.FlowStatus(flowID)
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

	resultCommand := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowId + ":" + stepId, "snapshotData")
	vals, err := resultCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get flow " + flowId + " and step " + stepId + " snapshot data  error"))
		log.Errorf("Get flow " + flowId + " and step " + stepId + " snapshot data  error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%s", vals)
	}
}

func GetInstanceSteps(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowId := params.ByName("flowID")
	log.Info("Get instance steps, flow " + flowId)

	service.ReditClient.SRem("flows", flowId)
	sliceCommand := service.ReditClient.Del("flows:" + flowId)

	vals, err := sliceCommand.Result()
	if err != nil {
		util.HandlerErrorResponse(response, http.StatusInternalServerError, err)
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

	metadata, err := GetSnapshotMetdata(flowID)
	if err != nil {
		util.HandleInternalError(response, errors.New("Get flow " + flowID + " snapshot metadata error"))
		log.Errorf("Get flow " + flowID + " snapshot metadata error: %v", err)
		return
	}
	response.Header().Set("Content-Type", "application/json")
	jsonFlow, _ := json.Marshal(metadata)
	fmt.Fprintf(response, "%s", jsonFlow)

}

func GetSnapshotMetdata(flowID string) (map[string]string, error) {
	statusComamnd := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "status")
	stateComamnd := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "state")
	dateCommand := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "date")
	idComamnd := service.ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "id")

	metadata := make(map[string]string)
	metadata["flowID"] = flowID
	status, err := statusComamnd.Result()
	if err == nil {
		metadata["status"] = status
	}

	state, err := stateComamnd.Result()
	if err == nil {
		metadata["state"] = state
	}

	date, err := dateCommand.Result()
	if err == nil {
		metadata["date"] = date
	}

	id, err := idComamnd.Result()
	if err == nil {
		metadata["id"] = id
	}
	return metadata, err
}

func ListSnapshots(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	resultCommand := service.ReditClient.SInter(SNAPSHOTS_FLOWS_KEY)
	vals, err := resultCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get snapshot keys error"))
		log.Errorf("Get snapshot keys error error: %v", err)
		return
	} else {
		results := make([]map[string]string, len(vals))

		for index, name := range vals {
			metadata, err := GetSnapshotMetdata(name)
			if err != nil {
				util.HandleInternalError(response, errors.New("Get flow " + name + " snapshot metadata error"))
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
	command := service.ReditClient.Keys("flow:*")
	vals, err := command.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get flow keys error"))
		log.Errorf("Get flow keys error: %v", err)
		return
	} else {

		results := make([]map[string]string, len(vals))

		for index, element := range vals {
			result := service.ReditClient.HGetAll(element)
			allResult, getallErr := result.Result()
			if getallErr != nil {
				util.HandleInternalError(response, errors.New("Get flow key data error"))
				log.Errorf("Get flow key data error: %v", err)
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
		util.HandleInternalError(response, errors.New("Read body error"))
		log.Errorf("Read body error: %v", err)
		return
	}

	contentMap := map[string]string{}
	jsonerr := json.Unmarshal(content, &contentMap)
	if jsonerr != nil {
		util.HandleInternalError(response, errors.New("Unmarshal post body error"))
		log.Errorf("Unmarshal post body error %v", err)
		return
	}

	flowID := contentMap["flowID"]
	id := contentMap["id"]
	state := contentMap["state"]
	status := contentMap["status"]
	stepData := contentMap["stepData"]

	change := make(map[string]string)
	change["flowID"] = flowID
	change["id"] = id
	change["stepData"] = stepData
	change["state"] = state
	change["status"] = status
	change["date"] = time.Now().String()

	key := STEP_NAMESPACE + flowID + ":" + id

	//TODO error handling
	statusMap := make(map[string]string)
	statusMap["status"] = status
	service.ReditClient.HMSet(FLOW_NAMESPACE + flowID, statusMap)
	service.ReditClient.SAdd(STEP_FLOWS_KEY, flowID + ":" + id)
	service.ReditClient.HMSet(key, change)
	pushCommand := service.ReditClient.RPush(STEPS_NAMESPACE + flowID, key)
	vals, err := pushCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Save changes error"))
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
		util.HandleInternalError(response, errors.New("Read body error"))
		log.Errorf("Read body error: %v", err)
		return
	}

	contentMap := make(map[string]string)

	jsonerr := json.Unmarshal(content, &contentMap)
	if jsonerr != nil {
		util.HandleInternalError(response, errors.New("Unmarshal post body error"))
		log.Errorf("Unmarshal post body error %v", err)
		return
	}

	flowID := contentMap["flowID"]
	id := contentMap["id"]
	state := contentMap["state"]
	status := contentMap["status"]
	snapshotData := contentMap["snapshotData"]

	snapshot := make(map[string]string)
	snapshot["flowID"] = flowID
	snapshot["id"] = id
	snapshot["snapshotData"] = snapshotData
	snapshot["state"] = state
	snapshot["status"] = status
	snapshot["date"] = time.Now().String()

	key := SNAPSHOT_NAMESPACE + flowID + ":" + id

	//TODO error handling
	service.ReditClient.SAdd(SNAPSHOTS_FLOWS_KEY, flowID + ":" + id)
	service.ReditClient.HMSet(key, snapshot)
	pushCommand := service.ReditClient.RPush(SNAPSHOTS_NAMESPACE + flowID, key)
	vals, err := pushCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Save snapshot changes error"))
		log.Errorf("Save snapshot changes error: %v", err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", vals)
	}
}

func DeleteFLow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Debug("Delete flow " + flowID)

	//retrive snapshot names
	snapshotsCommand := service.ReditClient.LRange(SNAPSHOTS_NAMESPACE + flowID, 0, -1)
	snapshots, err := snapshotsCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get snapshot names error"))
		log.Errorf("Get snapshot names error: %v", err)
		return
	} else {

		for _, snapshot := range snapshots {
			keysCommand := service.ReditClient.HKeys(snapshot)
			keys, err := keysCommand.Result()
			if err != nil {
				util.HandleInternalError(response, errors.New("Get snapshot keys error"))
				log.Errorf("Get snapshot keys error: %v", err)
				return
			} else {
				for _, key := range keys {
					response := service.ReditClient.HDel(snapshot, key)
					log.Debug("snapshot flow key: " + flowID + " response: ", response.Val())
				}
			}
		}
	}

	remSnapShotRespComand := service.ReditClient.Del(SNAPSHOTS_NAMESPACE + flowID)
	log.Debug("snapshot: " + flowID + " response: ", remSnapShotRespComand.Val())
	remSnapShotResp, err := remSnapShotRespComand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Delete snapshot error"))
		log.Errorf("Get snapshot error: %v", err)
		return
	}
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusOK)
	fmt.Fprintf(response, "%d", remSnapShotResp)
}
