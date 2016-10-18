package main

import(
	"fmt"
	"github.com/julienschmidt/httprouter"
	"net/http"
	"encoding/json"
	"github.com/op/go-logging"
	"github.com/TIBCOSoftware/flogo-services/flow-store/flow"
	"github.com/TIBCOSoftware/flogo-services/flow-store/cmd"
)
var log = logging.MustGetLogger("main")

func main() {
	fmt.Println("Start flow go server")
	flowRouter := httprouter.New()

	flowRouter.GET("/ping", flow.Ping)

	//Flow
	flowRouter.GET("/flows", flow.ListAllFlow)
	flowRouter.GET("/flows/:id", flow.GetFlow)
	flowRouter.GET("/flows/:id/metadata", flow.GetFlowMetadata)
	flowRouter.POST("/flows", flow.SaveFlow)
	flowRouter.DELETE("/flows/:id", flow.DeleteFlow)

	if cmd.Port != nil && len(*cmd.Port) > 0 {
		log.Info("Started server on localhost:" + *cmd.Port)
		http.ListenAndServe(":"+*cmd.Port, &FlowServer{flowRouter})
	} else {
		log.Info("Started server on localhost:9090")
		http.ListenAndServe(":9090", &FlowServer{flowRouter})

	}}


type FlowServer struct {
	r *httprouter.Router
}

func (s *FlowServer) ServeHTTP(rw http.ResponseWriter, req *http.Request) {

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
