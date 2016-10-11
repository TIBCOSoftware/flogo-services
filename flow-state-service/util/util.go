package util

import (
	"strings"
	"net/http"
	"encoding/json"
	"fmt"
	"github.com/TIBCOSoftware/flogo-services/flow-state-service/stateerror"
)

func Contains(s []int64, e int64) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}


func StringContains(s []string, e string) bool {
	for _, a := range s {
		if strings.EqualFold(a, e) {
			return true
		}
	}
	return false
}

func HandlerErrorResponse(response http.ResponseWriter, code int, err error) {
	flowErorr := ConstructError(err, code, "common")
	returnApi, _ := json.Marshal(flowErorr)
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(code)
	fmt.Fprintf(response, "%s", returnApi)
}

func ConstructError(err error, code int, errType string) stateerror.StateError {

	return stateerror.StateError{
		Code: code,
		Message: err.Error(),
		Type: errType,
	}

}