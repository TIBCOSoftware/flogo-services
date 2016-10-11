package stateflow

import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
	"github.com/op/go-logging"
	"encoding/json"
	"strings"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service"

	"github.com/TIBCOSoftware/flogo-services/flow-state-service/util"
)

var log = logging.MustGetLogger("flow")

func ListAllFlowStatus(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Debug("List all flows status")
	command := service.ReditClient.Keys("flow:*")
	vals, err := command.Result()
	if err != nil {
		util.HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {

		results := make([]map[string]string, len(vals))

		for index, element := range vals {
			result := service.ReditClient.HGetAll(element)
			allResult, getallErr := result.Result();
			if getallErr != nil {
				util.HandlerErrorResponse(response, http.StatusInternalServerError, getallErr)
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

func GetFlowStatus(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	log.Info("Get flow status")

	flowID := params.ByName("flowID")
	//command := service.ReditClient.HGet("flow:" + flowID, "status")
	//
	//results, err := command.Result()
	metadata, err := FlowStatus(flowID)
	if err != nil {
		util.HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {
		response.Header().Set("Content-Type", "application/json")
		response.WriteHeader(http.StatusOK)
		jsonFlow, _ := json.Marshal(metadata)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func FlowStatus(flowID string) (map[string]string, error) {
	command := service.ReditClient.HGet("flow:" + flowID, "status")
	vals, err := command.Result()
	if err != nil {
		return nil, err
	} else {
		metadata := make(map[string]string)
		metadata["id"] = flowID
		metadata["status"] = vals
		return metadata, nil
	}
}

func DeleteFlow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {

	log.Info("Delete flow..")
	id := params.ByName("flowID")

	command := service.ReditClient.HKeys("flow:" + id)
	vals, err := command.Result()
	if err != nil {
		util.HandlerErrorResponse(response, http.StatusInternalServerError, err)
		return
	} else {
		for _, element := range vals {
			result := service.ReditClient.HDel("flow:" + id, element)
			allResult, getallErr := result.Result();
			if getallErr != nil {
				util.HandlerErrorResponse(response, http.StatusInternalServerError, getallErr)
				return
			} else {
				response.Header().Set("Content-Type", "application/json")
				response.WriteHeader(http.StatusOK)
				fmt.Fprintf(response, "%d", allResult)
			}
		}
	}

}
