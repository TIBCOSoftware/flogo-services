package snapshot

import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
	"github.com/op/go-logging"
	"encoding/json"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/instance"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/util"
	"github.com/pkg/errors"
)

var log = logging.MustGetLogger("snapshot")

var SNAPSHOT_NAMESPACE = "snapshot:";

var SNAPSHOTS_NAMESPACE = "snapshots:";

var SNAPSHOTS_FLOWS_KEY = "snapshotFlows";

func GetSnapshotStep(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("Get snapshop step flow " + flowID)

	sliceCommand := service.ReditClient.HGetAll(SNAPSHOT_NAMESPACE + flowID)

	vals, err := sliceCommand.Result()
	if err != nil {
		util.HandleInternalError(response, errors.New("Get snapshot steps error"))
		log.Errorf("Get snapshot steps error: %v", err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		jsonFlow, _ := json.Marshal(vals)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}

func FlowMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("Get snapshot metadata, flow id: " + flowID)
	metadata, err := instance.GetSnapshotMetdata(flowID)
	if err != nil {
		util.HandleInternalError(response, errors.New("Get snapshot metadata error"))
		log.Errorf("Get snapshot metadata error: %v", err)
		return
	}
	response.Header().Set("Content-Type", "application/json")
	jsonFlow, _ := json.Marshal(metadata)
	fmt.Fprintf(response, "%s", jsonFlow)
}

func ListSnapshots(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	instance.ListSnapshots(response, request, params)
}

func PostSnapshot(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	instance.POSTSnapshot(response, request, params)
}

func DeleteFlow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	instance.DeleteFLow(response, request, params)
}