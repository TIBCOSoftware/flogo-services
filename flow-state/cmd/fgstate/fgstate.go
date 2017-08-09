package main

import (
	"fmt"
	"github.com/julienschmidt/httprouter"
	"net/http"
	"encoding/json"
	"github.com/TIBCOSoftware/flogo-services/flow-state/flowinstance"
	"flag"
	"github.com/TIBCOSoftware/flogo-lib/logger"
)

var log = logger.GetLogger("main")
var Port = flag.String("p", "9098", "The port of the server")
func init() {
	log.Info("Starting parse cmd line paramters")
	flag.Parse() // get the arguments from command line
}

func main() {
	fmt.Println("Start flow states go server")
	stateRouter := httprouter.New()

	// TODO remove all non /v1/ apis once web uses versioning
	//Ping
	stateRouter.GET("/v1/ping", flowinstance.Ping)
	stateRouter.GET("/ping", flowinstance.Ping)

	//get all steps for the instance
	stateRouter.GET("/v1/instances/:flowID/steps", flowinstance.ListFlowSteps)
	stateRouter.GET("/instances/:flowID/steps", flowinstance.ListFlowSteps)

	//Instance resource
	stateRouter.GET("/v1/instances/:flowID/status", flowinstance.GetSnapshotStatus)
	stateRouter.GET("/instances/:flowID/status", flowinstance.GetSnapshotStatus)

	stateRouter.GET("/v1/instances/:flowID", flowinstance.ListFlowSteps)
	stateRouter.GET("/instances/:flowID", flowinstance.ListFlowSteps)

	// Delete flow
	stateRouter.DELETE("/v1/instances/:flowID", flowinstance.DeleteFLow)
	stateRouter.DELETE("/instances/:flowID", flowinstance.DeleteFLow)

	//Get the specified snapshot
	stateRouter.GET("/v1/instances/:flowID/snapshot/:snapshotID", flowinstance.GetSnapshotStep)
	stateRouter.GET("/instances/:flowID/snapshot/:snapshotID", flowinstance.GetSnapshotStep)

	// Get flow metadata
	stateRouter.GET("/v1/instances/:flowID/metadata", flowinstance.FlowMetadata)
	stateRouter.GET("/instances/:flowID/metadata", flowinstance.FlowMetadata)

	// Save a new Snapshot
	stateRouter.POST("/v1/instances/snapshot", flowinstance.POSTSnapshot)
	stateRouter.POST("/instances/snapshot", flowinstance.POSTSnapshot)

	// Save a new Step
	stateRouter.POST("/v1/instances/steps", flowinstance.PostChange)
	stateRouter.POST("/instances/steps", flowinstance.PostChange)



	// Deprecated APIs

	// MOVED TO flow-store
	// stateRouter.GET("/v1/flows", flowinstance.ListAllFlowStatus)
	// stateRouter.GET("/flows", flowinstance.ListAllFlowStatus)

	// USING POST "/v1/instances/snapshot" instead
	// stateRouter.POST("/v1/instances/:id/snapshot", flowinstance.POSTSnapshot)

	// USING POST "/v1/instances/steps" instead
	// stateRouter.POST("/v1/instances/:id/step", flowinstance.PostChange)

	// USING GET "/v1/instances/:flowID/status" instead
	// stateRouter.GET("/v1/instances/:id/status", flowinstance.GetSnapshotStatus)

	// USING GET "/v1/instances/:flowID" instead
	// stateRouter.GET("/v1/instances/:id", flowinstance.ListFlowSteps)

	// USING GET "/v1/instances/:flowID/snapshot/:snapshotID" instead
	// stateRouter.GET("/instances/:flowID/snapshot/:id", flowinstance.GetSnapshotStep)

	// MOVED to flow-store
	// stateRouter.GET("/flows/:flowID/status", flowinstance.GetFlowStatus)

	// MOVED to flow-store
	// stateRouter.DELETE("/flows/:flowID", flowinstance.DeleteFlow)

	// USING "/v1/instances/steps" instead
	// stateRouter.POST("/instances", flowinstance.PostChange)

	// USING GET "/v1/instances/:flowID/metadata" instead
	// stateRouter.GET("/instances", flowinstance.FlowMetadata)

	// NOT USED
	//stateRouter.GET("/instances/snapshots", snapshot.ListSnapshots)

	// USING
	//stateRouter.GET("/instances/snapshots", flowinstance.ListSnapshots)

	//Snapshot
	// NOT USED
	//stateRouter.GET("/snapshots", instance.ListSnapshots)

	// NOT USED
	//stateRouter.GET("/snapshots", flowinstance.ListSnapshots)

	// NOT USED
	//stateRouter.GET("/snapshot/:id", flowinstance.GetSnapshotStep)

	// NOT USED
	//stateRouter.GET("/snapshot/:id/metadata", flowinstance.FlowMetadata)

	// NOT USED
	//stateRouter.DELETE("/snapshots/:flowID", flowinstance.DeleteFlow)

	// NOT USED
	//stateRouter.POST("/snapshots/snapshot", flowinstance.POSTSnapshot)

	//Step
	// NOT USED
	//stateRouter.GET("/steps/:id/rollup", flowinstance.ListRollup)

	// NOT USED
	//stateRouter.GET("/steps/:id/rollup/snapshot", flowinstance.ListRollupMetadata)

	// NOT USED
	//stateRouter.GET("/steps", flowinstance.ListAlllStepData)

	// NOT USED
	//stateRouter.GET("/steps/:id/metadata", flowinstance.ListStepData)

	// NOT USED
	//stateRouter.GET("/step/flow/:flowID/stepdata", flowinstance.ListFlowStepData)

	// NOT USED
	//stateRouter.GET("/step1/:flowid/stepids", flowinstance.ListAllFlowStepIds)

	// NOT USED
	//stateRouter.POST("/steps", flowinstance.PostChange)

	// NOT USED
	//stateRouter.DELETE("/steps/:flowid", flowinstance.DeleteSteps)

	if Port != nil && len(*Port) > 0 {
		log.Info("Started server on localhost:" + *Port)
		http.ListenAndServe(":" + *Port, &FlowServer{stateRouter})
	} else {
		log.Info("Started server on localhost:9090")
		http.ListenAndServe(":9090", &FlowServer{stateRouter})

	}
}

type FlowServer struct {
	r *httprouter.Router
}

func (s *FlowServer) ServeHTTP(rw http.ResponseWriter, req *http.Request) {

	//Catching unexpected error.
	defer func() {
		if x := recover(); x != nil {
			log.Error(x)
			rw.WriteHeader(http.StatusInternalServerError)
			err, _ := json.Marshal(x)
			rw.Write(err)
		}
	}()

	if origin := req.Header.Get("Origin"); origin != "" {
		rw.Header().Set("Access-Control-Allow-Origin", "*")
		rw.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		rw.Header().Set("Access-Control-Allow-Headers",
			"Accept, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, X-Atmosphere-Remote-User,X-Atmosphere-Token")
		rw.Header().Set("Access-Control-Allow-Credentials", "true")
	}

	// Stop here if its Preflighted OPTIONS request
	if req.Method == "OPTIONS" {
		return
	}

	// Lets Gorilla work
	s.r.ServeHTTP(rw, req)
}