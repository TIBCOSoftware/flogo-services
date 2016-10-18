package flowinstance

import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
	"encoding/json"
	"errors"
	"github.com/TIBCOSoftware/flogo-services/flow-state/persistence"
)


var SNAPSHOT_NAMESPACE = "snapshot:";

var SNAPSHOTS_NAMESPACE = "snapshots:";

var SNAPSHOTS_FLOWS_KEY = "snapshotFlows";

func GetFlowSnapshot(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
	flowID := params.ByName("flowID")
	log.Info("Get snapshop step flow " + flowID)

	sliceCommand := persistence.ReditClient.HGetAll(SNAPSHOT_NAMESPACE + flowID)

	vals, err := sliceCommand.Result()
	if err != nil {
		HandleInternalError(response, errors.New("Get snapshot steps error"))
		log.Errorf("Get snapshot steps error: %v", err)
		return
	} else {
		log.Info(vals)
		response.Header().Set("Content-Type", "application/json")
		jsonFlow, _ := json.Marshal(vals)
		fmt.Fprintf(response, "%s", jsonFlow)
	}
}
//
//func FlowMetadata(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
//	flowID := params.ByName("flowID")
//	log.Info("Get snapshot metadata, flow id: " + flowID)
//	metadata, err := GetSnapshotMetdata(flowID)
//	if err != nil {
//		util.HandleInternalError(response, errors.New("Get snapshot metadata error"))
//		log.Errorf("Get snapshot metadata error: %v", err)
//		return
//	}
//	response.Header().Set("Content-Type", "application/json")
//	jsonFlow, _ := json.Marshal(metadata)
//	fmt.Fprintf(response, "%s", jsonFlow)
//}

//func ListSnapshots(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
//	ListSnapshots(response, request, params)
//}
//
//func PostSnapshot(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
//	instance.POSTSnapshot(response, request, params)
//}
//
//func DeleteFlow(response http.ResponseWriter, request *http.Request, params httprouter.Params) {
//	instance.DeleteFLow(response, request, params)
//}