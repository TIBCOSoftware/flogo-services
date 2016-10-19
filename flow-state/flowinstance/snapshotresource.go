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

	sliceCommand := persistence.NewClient().HGetAll(SNAPSHOT_NAMESPACE + flowID)

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