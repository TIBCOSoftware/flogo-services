package service

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
	"gopkg.in/redis.v4"
	"github.com/TIBCOSoftware/flogo-services/flow-store-service/cmd"
	"github.com/TIBCOSoftware/flogo-services/flow-store-service/model"
	"github.com/TIBCOSoftware/flogo-services/flow-store-service/flowerror"
)

var ReditClient = redis.NewClient(&redis.Options{Addr: *cmd.RedisAddr, Password:"",})

var flowId uint64 = 0
var log = logging.MustGetLogger("service")

func ListAllFlow(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Info("List all flows")
	sliceCommand := ReditClient.SInter("flows")
	vals, err := sliceCommand.Result()
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {

		flows := make([]map[string]string, len(vals))
		for index, element := range vals {
			metdata, err := getMetdata(element)
			if err != nil {
				HandlerErrorResponse(response, http.StatusInternalServerError, err)
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
	log.Info("Get flow " + id)

	sliceCommand := ReditClient.HGet("flow:" + id, "flow")
	vals, err := sliceCommand.Result()
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		fmt.Fprintf(response, "%s", vals)
	}
}

func GetFlowMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	id := params.ByName("id")
	log.Info("Get flow " + id)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusOK)
	result, err := getMetdata(id)
	jsonFlow, _ := json.Marshal(result)
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	}
	fmt.Fprintf(response, "%s", jsonFlow)
}

func SaveFlow(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Info("Running save flow.")
	flowContent, err := ioutil.ReadAll(request.Body)
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	}

	flowInfo := &model.FlowInfo{}
	unmarshalErr := json.Unmarshal(flowContent, flowInfo)

	if unmarshalErr != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, unmarshalErr)
		return
	}
	var id = flowInfo.Id
	if nilOrEmpty(flowInfo.Id) {
		id = strconv.FormatUint(incrementalFlowId(), 10)
	}

	log.Info("Id is:" , id)
	flowMap := make(map[string]string)
	flowMap["creationDate"] = time.Now().String()
	flowMap["id"] = id
	flowMap["name"] = flowInfo.Name
	flowMap["description"] = flowInfo.Description

	flowMap["flow"] = flowInfo.Flow

	flowCommand := ReditClient.HMSet("flow:" + id, flowMap);
	vals, err := flowCommand.Result()
	if err != nil {
		HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {
		log.Info(vals)
		flowsCommand := ReditClient.SAdd("flows", id)
		_, err := flowsCommand.Result()
		if err != nil {
			HandlerErrorResponse(response, http.StatusInternalServerError, err)
			return
		} else {
			result, err := getMetdata(id)
			jsonFlow, _ := json.Marshal(result)
			if err != nil {
				HandlerErrorResponse(response, http.StatusInternalServerError, err)
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

	ReditClient.SRem("flows", id)
	sliceCommand := ReditClient.Del("flows:" + id)

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

func getMetdata(id string) (map[string]string, error) {

	descCommand := ReditClient.HGet("flow:" + id, "description")
	nameCommand := ReditClient.HGet("flow:" + id, "name");
	createDateCommand := ReditClient.HGet("flow:" + id, "creationDate")

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

func ConstructError(err error, code int, errType string) flowerror.FlowError {

	return flowerror.FlowError{
		Code: code,
		Message: err.Error(),
		Type: errType,
	}

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