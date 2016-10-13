package main

import (
	"fmt"
	"github.com/julienschmidt/httprouter"
	"net/http"
	"encoding/json"
	"github.com/op/go-logging"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/stateflow"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/instance"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/snapshot"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/service/step"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/cmd"
	stateping "github.com/TIBCOSoftware/flogo-services/flow-state-service/service/ping"
)

var log = logging.MustGetLogger("main")

func main() {
	fmt.Println("Start flow states go server")
	stateRouter := httprouter.New()

	//Flow resource
	stateRouter.GET("/flows", stateflow.ListAllFlowStatus)
	stateRouter.GET("/flows/:flowID/status", stateflow.GetFlowStatus)
	stateRouter.DELETE("/flows/:flowID", stateflow.DeleteFlow)

	//Instance resource
	stateRouter.GET("/instances/:flowID/status", instance.GetSnapshotStatus)

	//stateRouter.GET("/instances/:flowID", instance.ListInstanceStatus)
	//TODO
	stateRouter.GET("/instances/:flowID", instance.ListFlowSteps)
	stateRouter.GET("/instances/:flowID/snapshot/:id", instance.GetSnapshotStep)
	//stateRouter.GET("/instances/:flowID/steps", instance.GetInstanceSteps)

	stateRouter.GET("/instances/:flowID/steps", instance.ListFlowSteps)

	stateRouter.GET("/instances/:flowID/metadata", instance.FlowMetadata)

	stateRouter.POST("/instances", instance.PostChange)
	stateRouter.DELETE("/instances/:flowID", instance.DeleteFLow)

	//stateRouter.GET("/instances/snapshots", snapshot.ListSnapshots)
	stateRouter.GET("/instances", snapshot.FlowMetadata)
	//TODO change to another interface
	stateRouter.GET("/instance/instances", instance.ListSnapshots)
	stateRouter.POST("/instances/snapshot", instance.POSTSnapshot)
	stateRouter.POST("/instances/steps", instance.PostChange)
	//Ping
	stateRouter.GET("/ping", stateping.Ping)

	//Snapshot
	//stateRouter.GET("/snapshots", instance.ListSnapshots)
	stateRouter.GET("/snapshots", snapshot.ListSnapshots)
	stateRouter.POST("/snapshots/snapshot", instance.POSTSnapshot)
	stateRouter.GET("/snapshot/:id", snapshot.GetSnapshotStep)
	stateRouter.GET("/snapshot/:id/metadata", snapshot.FlowMetadata)
	stateRouter.DELETE("/snapshots/:flowID", snapshot.DeleteFlow)

	//Step
	stateRouter.GET("/steps/:id/rollup", step.ListRollup)
	stateRouter.GET("/steps/:id/rollup/snapshot", step.ListRollupMetadata)
	stateRouter.GET("/steps", step.ListAlllStepData)
	stateRouter.GET("/steps/:id/metadata", step.ListStepData)
	stateRouter.GET("/step/flow/:flowID/stepdata", step.ListFlowStepData)
	stateRouter.GET("/step1/:flowid/stepids", step.ListAllFlowStepIds)

	stateRouter.POST("/steps", step.PostChange)
	stateRouter.DELETE("/steps/:flowid", step.DeleteSteps)

	if cmd.Port != nil && len(*cmd.Port) > 0 {
		log.Info("Started server on localhost:" + *cmd.Port)
		http.ListenAndServe(":" + *cmd.Port, &FlowServer{stateRouter})
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
			log.Fatal(x)
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