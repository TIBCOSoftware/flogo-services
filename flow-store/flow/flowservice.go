package flow

import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
	"github.com/op/go-logging"
	"encoding/json"
	"io/ioutil"
	"time"
	"sync/atomic"
	"strconv"
	"errors"
	"github.com/TIBCOSoftware/flogo-services/flow-store/persistence"
)


var flowId uint64 = 0
var log = logging.MustGetLogger("service")

func ListAllFlow(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Debug("List all flows")
	sliceCommand := persistence.ReditClient.SInter("flows")
	vals, err := sliceCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("List all flows error"))
		log.Error("List all flow error :%v", err)
		return
	} else {

		flows := make([]map[string]string, len(vals))
		for index, element := range vals {
			metdata, err := getMetdata(element)
			if err != nil {
				HandleInternalError(response, errors.New("Get " + element + " metadata error"))
				log.Error("Get " + element + " metadata error :%v", err)
				return
			} else {
				flows[index] = metdata
			}
		}

		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(flows)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func GetFlow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	log.Debug("Get flow " + id)

	sliceCommand := persistence.ReditClient.HGet("flow:" + id, "flow")
	vals, err := sliceCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get flow " + id + " error"))
		log.Error("Get flow " + id + " error :%v", err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%s", vals)
	}
}

func GetFlowMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	log.Debug("Get flow " + id)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusOK)
	result, err := getMetdata(id)
	jsonFlow, _ := json.Marshal(result)
	if err != nil {
		HandleInternalError(response, errors.New("Get flow " + id + " metadata error"))
		log.Error("Get flow " + id + " metadata error :%v", err)
		return
	}
	fmt.Fprintf(response, "%s", jsonFlow)
}

func SaveFlow(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Debug("Running save flow.")
	flowContent, err := ioutil.ReadAll(request.Body)
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	}

	flowInfo := map[string]interface{}{}
	unmarshalErr := json.Unmarshal(flowContent, &flowInfo)
	if unmarshalErr != nil {
		HandleInternalError(response, errors.New("Unmarshal flow body error while save flow"))
		log.Error("Unmarshal flow body error while save flow:%v", unmarshalErr)
		return
	}
	var id string
	var idtmp = flowInfo["id"]
	if (idtmp != nil && nilOrEmpty(idtmp.(string)) ) {
		id = idtmp.(string)
	} else {
		id = strconv.FormatUint(incrementalFlowId(), 10)
	}

	log.Info("Id is:", id)
	flowMap := make(map[string]string)
	flowMap["creationDate"] = time.Now().String()
	flowMap["id"] = id
	flowMap["name"] = flowInfo["name"].(string)
	flowMap["description"] = flowInfo["description"].(string)
	b, err := json.Marshal(flowInfo["flow"])
	if err != nil {
		HandleInternalError(response, errors.New("Parse flow error"))
		log.Error("Parse flow error:%v", err)
		return
	}
	flowMap["flow"] = string(b)

	flowCommand := persistence.ReditClient.HMSet("flow:" + id, flowMap);
	vals, err := flowCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get flow from BD error, flow id: " + id))
		log.Error("Get flow from BD error, flow id: " + id + " :%v", err)
		return
	} else {
		log.Info(vals)
		flowsCommand := persistence.ReditClient.SAdd("flows", id)
		_, err := flowsCommand.Result()
		if err != nil {
			HandleInternalError(response, errors.New("Save flow" + id + " into DB error."))
			log.Error("Save flow" + id + " into DB error :%v", err)
			return
		} else {
			result, err := getMetdata(id)
			jsonFlow, _ := json.Marshal(result)
			if err != nil {
				HandleInternalError(response, errors.New("Get flow " + id + " metadata error"))
				log.Error("Get flow " + id + " metadata error :%v", err)
				return
			}
			response.Header().Set("Content-Type", "application/json")
			response.WriteHeader(http.StatusOK)
			log.Info("Result is:", string(jsonFlow))
			fmt.Fprintf(response, "%s", jsonFlow)
		}
	}
}

func DeleteFlow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	log.Info("Delete flow " + id)

	persistence.ReditClient.SRem("flows", id)
	sliceCommand := persistence.ReditClient.Del("flows:" + id)
	vals, err := sliceCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Delete flow " + id + " error"))
		log.Error("Delete flow " + id + " error :%v", err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%d", vals)
	}
}

func getMetdata(id string) (map[string]string, error) {
	descCommand := persistence.ReditClient.HGet("flow:" + id, "description")
	nameCommand := persistence.ReditClient.HGet("flow:" + id, "name");
	createDateCommand := persistence.ReditClient.HGet("flow:" + id, "creationDate")

	resultMap := make(map[string]string)
	resultMap["id"] = id

	desc, derr := descCommand.Result()
	if derr != nil {
		return nil, derr
	} else {
		resultMap["description"] = desc
	}

	name, nerr := nameCommand.Result()
	if nerr != nil {
		return nil, nerr
	} else {
		resultMap["name"] = name
	}

	createDate, cerr := createDateCommand.Result()
	if cerr != nil {
		return nil, cerr
	} else {
		resultMap["creationDate"] = createDate
	}
	return resultMap, nil
}

func HandlerErrorResponse(response http.ResponseWriter, code int, err error) {
	flowErorr := ConstructError(err, code, "common")
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(code)
	fmt.Fprintf(response, "%s", returnApi)
}

func ConstructError(err error, code int, errType string) FlowError {

	return FlowError{
		Code: code,
		Message: err.Error(),
		Type: errType,
	}

}

func HandlerErrorResWithType(response http.ResponseWriter, code int, err error, errorType string) {
	flowErorr := ConstructError(err, code, errorType)
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(code)
	fmt.Fprintf(response, "%s", returnApi)
}

func HandleInternalError(response http.ResponseWriter, err error) {
	flowErorr := ConstructError(err, http.StatusInternalServerError, InternalError)
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(response, "%s", returnApi)
}

func nilOrEmpty(str string) bool {
	if len(str) <= 0 {
		return true
	}
	return false
}

func incrementalFlowId() uint64 {
	return atomic.AddUint64(&flowId, 1)
}