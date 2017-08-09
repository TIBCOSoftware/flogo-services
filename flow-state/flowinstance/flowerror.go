package flowinstance

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type StateError struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Type    string `json:type`
}

const (
	InternalError = "Internal DB Error"
)

func ConstructError(err error, code int, errType string) StateError {

	return StateError{
		Code:    code,
		Message: err.Error(),
		Type:    errType,
	}

}

func HandlerErrorResponse(response http.ResponseWriter, code int, err error) {
	flowErorr := ConstructError(err, code, "common")
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(code)
	fmt.Fprintf(response, "%s", returnApi)
}

func HandlerErrorResWithType(response http.ResponseWriter, code int, err error, errorType string) {
	flowErorr := ConstructError(err, code, errorType)
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(code)
	fmt.Fprintf(response, "%s", returnApi)
}

func HandleInternalError(response http.ResponseWriter, err error) {
	flowErorr := ConstructError(err, http.StatusInternalServerError, InternalError)
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(response, "%s", returnApi)
}
