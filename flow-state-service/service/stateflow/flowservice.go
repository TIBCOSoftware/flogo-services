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
	"github.com/pkg/errors"
)

var log = logging.MustGetLogger("flow")

func ListAllFlowStatus(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	log.Debug("List all flows status")
	command := service.ReditClient.Keys("flow:*")
	flowResults, err := command.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get flow from DB error"))
		log.Errorf("Get all flow status error: %v", err)
		return
	} else {

		results := make([]map[string]string, len(flowResults))

		for index, element := range flowResults {
			result := service.ReditClient.HGetAll(element)
			allResult, getallErr := result.Result();
			if getallErr != nil {
				util.HandleInternalError(response, errors.New("Get flow " + element + "from DB error"))
				log.Errorf("Get flow " + element + "from DB error: %v", err)
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
	metadata, err := FlowStatus(flowID)
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
		util.HandleInternalError(response, errors.New("Get keys error while delete flow " + id + " error"))
		log.Errorf("Get keys error while delete flow " + id + " error: %v", err)
		return
	} else {
		for _, element := range vals {
			result := service.ReditClient.HDel("flow:" + id, element)
			allResult, getallErr := result.Result();
			if getallErr != nil {
				util.HandleInternalError(response, errors.New("Delete flow " + id + " error"))
				log.Errorf("Delete flow " + id + " error: %v", err)
				return
			} else {
				response.Header().Set("Content-Type", "application/json")
				response.WriteHeader(http.StatusOK)
				fmt.Fprintf(response, "%d", allResult)
			}
		}
	}

}
