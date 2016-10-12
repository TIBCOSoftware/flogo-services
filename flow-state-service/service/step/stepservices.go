package step

import (
	"github.com/op/go-logging"
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
	"errors"
	"strings"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/model"
	"strconv"
	"encoding/json"
	"io/ioutil"
	"time"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/instance"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/util"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service"
)

var STEP_NAMESPACE = "step:"
var STEPS_NAMESPACE = "steps:"
var STEP_FLOWS_KEY = "stepFlows"
var log = logging.MustGetLogger("snapshot")

func ListRollup(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	log.Debug("List Rollup" + id)
	rollup, err := RollUp(id)

	if err == nil {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(rollup)
		fmt.Fprintf(response, "%s", jsonFlow)
	} else {
		util.HandleInternalError(response, errors.New("List rollup error"))
		log.Errorf("List rollup error: %v", err)
		return
	}

}

func ListRollupMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	if id != "" && len(id) > 0 {
		tokens := strings.Split(id, ":")
		if len(tokens) != 2 {
			util.HandleInternalError(response, errors.New("Invalid id format: " + id + " should be <flowid>:<sid>"))
			log.Errorf("Invalid id format: " + id + " should be <flowid>:<sid>: %v")
			return

		}
		rollup, err := RollUp(id)

		if err == nil {
			response.Header().Set("Content-Type", "application/json")
			response.WriteHeader(http.StatusOK)
			jsonFlow, _ := json.Marshal(rollup.Snapshot)
			fmt.Fprintf(response, "%s", jsonFlow)
		} else {
			util.HandleInternalError(response, errors.New("List rollup error"))
			log.Errorf("List rollup error: %v", err)
			return
		}
	}

}

func ListAlllStepData(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	sliceCommand := service.ReditClient.SInter(STEP_FLOWS_KEY)

	var results = []map[string]string{}

	vals, err := sliceCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get steps flows keys error"))
		log.Errorf("Get steps flows keys error: %v", err)
		return
	} else {

		for _, v := range vals {
			step, err := GetStep(v)
			if err != nil {
				util.HandleInternalError(response, errors.New("Get steps error"))
				log.Errorf("Get steps error: %v", err)
				return
			} else {
				appendResult := append(results, step)
				results = appendResult
			}
		}

		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(results)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListStepData(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")

	stepData, err := GetStep(id)
	if err != nil {
		util.HandleInternalError(response, errors.New("Get steps error"))
		log.Errorf("Get steps error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(stepData)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListFlowStepData(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("flowID")
	log.Debug("List flow step data " + id)

	if strings.Contains(id, ":") {
		util.HandleInternalError(response, errors.New("Invalid flow id format: " + id))
		return
	}

	stepsComamnd := service.ReditClient.LRange(STEPS_NAMESPACE + id, 0, -1)
	steps, err := stepsComamnd.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get steps " + id + " error"))
		log.Errorf("Get steps " + id + " error: %v", err)
		return
	} else {
		var results = []map[string]string{}
		for _, step := range steps {
			step, err := GetStep(step)
			if err != nil {
				util.HandleInternalError(response, errors.New("Get steps error"))
				log.Errorf("Get steps error: %v", err)
				return
			}
			appendResult := append(results, step)
			results = appendResult
		}

		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(results)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func ListAllFlowStepIds(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("flowid")
	log.Debug("list all flow step id " + id)
	sliceCommand := service.ReditClient.LRange(STEPS_NAMESPACE + id, 0, -1)
	vals, err := sliceCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get steps " + id + " error"))
		log.Errorf("Get steps " + id + " error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(vals)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func PostChange(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	log.Info("Post changes flow ")
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
	stepInfo := contentMap["stepData"]

	val, err := SaveSteps(flowID, id, state, status, stepInfo)
	if err != nil {
		util.HandleInternalError(response, errors.New("Save steps error"))
		log.Errorf("Save steps error: %v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", val)
	}
}

func SaveSteps(flowID string, id string, state string, status string, stepInfo string) (int64, error) {
	changes := make(map[string]string)
	changes["flowID"] = flowID
	changes["id"] = id
	changes["stepData"] = stepInfo
	changes["state"] = state
	changes["status"] = status
	changes["date"] = time.Now().String()

	key := STEP_NAMESPACE + flowID + ":" + id;

	//TODO error handling
	statusMap := make(map[string]string)
	statusMap["status"] = status

	service.ReditClient.HMSet(instance.FLOW_NAMESPACE + flowID, statusMap)

	service.ReditClient.SAdd("stepFlows", flowID + ":" + id)
	service.ReditClient.HMSet(key, changes)

	pushCommand := service.ReditClient.RPush(STEPS_NAMESPACE + flowID, key)
	vals, err := pushCommand.Result()
	if err != nil {
		return 0, err
	} else {
		return vals, nil
	}
}

func DeleteSteps(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("flowid")
	log.Info("Delete steps " + id)

	stepsCommand := service.ReditClient.LRange(STEPS_NAMESPACE + id, 0, -1)

	steps, err := stepsCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get steps " + id + " error"))
		log.Errorf("Get steps " + id + " error: %v", err)
		return
	} else {
		for _, step := range steps {
			keysCommand := service.ReditClient.HKeys(step)
			keys, err := keysCommand.Result()
			if err != nil {
				util.HandlerErrorResponse(response, http.StatusInternalServerError, err)
				return
			} else {
				for _, key := range keys {
					responseCommand := service.ReditClient.HDel(step, key)
					log.Debug("step hash: " + key + " response: ", responseCommand.Val())
				}
			}

			remSnapShotFlowCommand := service.ReditClient.SRem(STEP_FLOWS_KEY, strings.Replace(step, STEP_NAMESPACE, "", 1))
			log.Debug("step flow key: " + id + " response: ", remSnapShotFlowCommand.Val());
		}

		remStepResp := service.ReditClient.Del(STEPS_NAMESPACE + id);
		log.Debug("step: " + id + " response: ", remStepResp.Val());

		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", remStepResp.Val())
	}
}

func RollUp(id string) (model.RollUpObj, error) {

	if id != "" && len(id) > 0 {
		tokens := strings.Split(id, ":")
		if len(tokens) != 2 {
			return model.RollUpObj{}, errors.New("Invalid id format: " + id + " should be <flowid>:<sid>")
		}

		intV, err := strconv.ParseInt(tokens[1], 10, 64)
		if err != nil {
			return model.RollUpObj{}, err
		}

		rolliup := RollUP(tokens[0], intV)
		return rolliup, nil
	} else {
		return model.RollUpObj{}, errors.New("Invalid id format: " + id + " should be <flowid>:<sid>")
	}
}

func RollUP(flowId string, stepId int64) model.RollUpObj {

	rollup := model.RollUpObj{}
	stepInfos, err := getStepInfo(flowId)
	if err == nil {
		//sort.Sort(stepInfos)
		for _, stepInfo := range stepInfos {
			stepData := stepInfo.StepData
			if stepInfo.ID <= stepId {
				if stepId == stepInfo.ID {
					rollup.ID = stepInfo.ID
					rollup.State = stepInfo.State
					rollup.Status = stepInfo.Status
					rollup.FlowURI = stepInfo.FlowID

					rollup.Snapshot.State = stepInfo.State
					rollup.Snapshot.Status = stepInfo.Status
					rollup.Snapshot.FlowURI = stepInfo.FlowID
				}

				addWorkItem(stepData, rollup)
				AddTaskAndLinkDatas(stepData, rollup)
				addAttribute(stepData, rollup)
			}
		}
	}

	return rollup

}

func getStepInfo(flowID string) ([]model.StepInfo, error) {
	stepsID, err := ListallFlowStpids(flowID)
	if err == nil {
		if len(stepsID) <= 0 {
			log.Info("Steps for flow: " + flowID + " not found")
		}

		if len(stepsID) > 0 {

			stepInfoLists := []model.StepInfo{}
			for _, stepId := range stepsID {
				changeJsonCommand := service.ReditClient.HGetAll(stepId)
				changeJson, err := changeJsonCommand.Result()
				log.Info("Change json, %v", changeJson)
				if err != nil {
					log.Error(err)
					return nil, err
				}

				if changeJson == nil {
					log.Info("Step: " + stepId + " not found")
				} else {
					stepInfo := model.StepInfo{}
					id, idErr := strconv.ParseInt(changeJson["id"], 10, 64)
					if idErr != nil {
						return nil, idErr
					} else {
						stepInfo.ID = id
					}
					stepInfo.Date = changeJson["date"]
					stepInfo.FlowID = changeJson["flowID"]
					state, stateErr := strconv.ParseInt(changeJson["state"], 10, 64)
					if stateErr != nil {
						return nil, stateErr
					} else {
						stepInfo.State = state
					}
					status, statusErr := strconv.ParseInt(changeJson["status"], 10, 64)
					if statusErr != nil {
						return nil, statusErr
					} else {
						stepInfo.Status = status
					}

					stepData := model.StepData{}
					stepDataStr := changeJson["stepData"]
					err := json.Unmarshal([]byte(stepDataStr), &stepData)
					if err != nil {
						log.Error(err)
						return nil, err
					}

					stepInfo.StepData = stepData

					result := append(stepInfoLists, stepInfo)
					stepInfoLists = result
				}

			}

			return stepInfoLists, nil;
		}

	} else {
		log.Error(err)
	}


	return []model.StepInfo{}, err

}

func ListallFlowStpids(flowID string) ([]string, error) {
	resultCommand := service.ReditClient.LRange(STEPS_NAMESPACE + flowID, 0, -1)
	return resultCommand.Result()
}

func addWorkItem(stepData model.StepData, rollupObj model.RollUpObj) {

	wqchanges := stepData.WqChanges
	if wqchanges != nil {
		for _, wqChange := range wqchanges {
			//id := wqChange.ID
			chgType := wqChange.ChgType
			//log.Debug("Work Item type: " + chgType + " id: " + id);

			if chgType == 1 {
				rollupObj.AddWorkQueueItem(wqChange.Wtem)
			} else if chgType == 2 {
				rollupObj.UpdateWorkQueueItem(wqChange.Wtem)
			} else if chgType == 3 {
				rollupObj.RemoveWorkQueueItem(wqChange.Wtem)
			}
		}
	}
}

func addAttribute(stepData model.StepData, rollupObj model.RollUpObj) {

	attributes := stepData.Attrs
	if attributes != nil {
		for _, attr := range attributes {
			//log.Debug("Attribute type: " + attr.ChgType() + " name: " + attr.Att.Name);
			if attr.ChgType == 1 {
				rollupObj.AddAttr(attr.Att)
			} else if attr.ChgType == 2 {
				rollupObj.UpdateAttr(attr.Att)
			} else if attr.ChgType == 3 {
				rollupObj.RemoveAttr(attr.Att)
			}
		}
	}
}

func AddTaskAndLinkDatas(stepData model.StepData, rollupObj model.RollUpObj) {
	//TaskChange
	tdChanges := stepData.TdChanges
	if tdChanges != nil {
		for _, tdChange := range tdChanges {
			//id := tdChange.ID
			chgType := tdChange.ChgType
			//log.Debug("Work Item type: " + chgType + " id: " + id);

			if chgType == 3 {
				rollupObj.RemoveTask(tdChange.ID)
			} else if chgType == 2 {
				rollupObj.AddTask(tdChange.TaskData)
			} else if chgType == 1 {
				rollupObj.UpdateTask(tdChange.TaskData)
			}
		}
	}

	//Link Change
	ldChanges := stepData.LdChanges
	if ldChanges != nil {
		for _, ldChange := range ldChanges {
			//id := ldChange.ID
			chgType := ldChange.ChgType
			//log.Debug("Work Item type: " + chgType + " id: " + id);

			if chgType == 3 {
				rollupObj.RemoveLink(ldChange.ID)
			} else if chgType == 2 {
				rollupObj.AddLink(ldChange.LinkData)
			} else if chgType == 1 {
				rollupObj.RemoveTask(ldChange.ID)
			}
		}
	}
}

func GetStep(id string) (map[string]string, error) {

	if strings.HasPrefix(id, STEP_NAMESPACE) {
		command := service.ReditClient.HGetAll(id)
		return command.Result()
	} else {
		command := service.ReditClient.HGetAll(STEP_NAMESPACE + id)
		return command.Result()
	}

}