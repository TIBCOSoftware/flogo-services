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

	//New release V1 Apis
	//create an instance
	stateRouter.POST("/v1/instances/:id/snapshot", flowinstance.POSTSnapshot)
	//store a step
	stateRouter.POST("/v1/instances/:id/step", flowinstance.PostChange)

	//get all steps for the instance
	stateRouter.GET("/v1/instances/:id/steps", flowinstance.ListFlowSteps)
	stateRouter.GET("/v1/instances/:id/status", flowinstance.GetSnapshotStatus)
	stateRouter.GET("/v1/instances/:id", flowinstance.ListFlowSteps)
	//get the specified snapshot
	stateRouter.GET("/v1/instances/:id/snapshots/:snapshotId", flowinstance.GetSnapshotStep)

	//Ping
	stateRouter.GET("/ping", flowinstance.Ping)
	//Instance resource
	stateRouter.GET("/instances/:flowID/status", flowinstance.GetSnapshotStatus)
	stateRouter.GET("/instances/:flowID/steps", flowinstance.ListFlowSteps)
	stateRouter.GET("/instances/:flowID", flowinstance.ListFlowSteps)
	stateRouter.GET("/instances/:flowID/snapshot/:id", flowinstance.GetSnapshotStep)
	stateRouter.POST("/instances/steps", flowinstance.PostChange)
	stateRouter.POST("/instances/snapshot", flowinstance.POSTSnapshot)


	//All apis below are deprecated.
	//Flow resource
	stateRouter.GET("/flows", flowinstance.ListAllFlowStatus)
	stateRouter.GET("/flows/:flowID/status", flowinstance.GetFlowStatus)
	stateRouter.DELETE("/flows/:flowID", flowinstance.DeleteFlow)

	stateRouter.GET("/instances/:flowID/metadata", flowinstance.FlowMetadata)

	stateRouter.POST("/instances", flowinstance.PostChange)
	stateRouter.DELETE("/instances/:flowID", flowinstance.DeleteFLow)

	//stateRouter.GET("/instances/snapshots", snapshot.ListSnapshots)
	stateRouter.GET("/instances", flowinstance.FlowMetadata)
	//TODO change to another interface
	stateRouter.GET("/instance/instances", flowinstance.ListSnapshots)

	//Snapshot
	//stateRouter.GET("/snapshots", instance.ListSnapshots)
	stateRouter.GET("/snapshots", flowinstance.ListSnapshots)
	stateRouter.GET("/snapshot/:id", flowinstance.GetSnapshotStep)
	stateRouter.GET("/snapshot/:id/metadata", flowinstance.FlowMetadata)
	stateRouter.DELETE("/snapshots/:flowID", flowinstance.DeleteFlow)
	stateRouter.POST("/snapshots/snapshot", flowinstance.POSTSnapshot)

	//Step
	stateRouter.GET("/steps/:id/rollup", flowinstance.ListRollup)
	stateRouter.GET("/steps/:id/rollup/snapshot", flowinstance.ListRollupMetadata)
	stateRouter.GET("/steps", flowinstance.ListAlllStepData)
	stateRouter.GET("/steps/:id/metadata", flowinstance.ListStepData)
	stateRouter.GET("/step/flow/:flowID/stepdata", flowinstance.ListFlowStepData)
	stateRouter.GET("/step1/:flowid/stepids", flowinstance.ListAllFlowStepIds)

	stateRouter.POST("/steps", flowinstance.PostChange)
	stateRouter.DELETE("/steps/:flowid", flowinstance.DeleteSteps)

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