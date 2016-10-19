package flowinstance
import (
	"net/http"
	"github.com/julienschmidt/httprouter"
	"fmt"
)

func Ping(response http.ResponseWriter, request *http.Request, _ httprouter.Params) {
	response.Header().Set("Content-Type", "application/json")
	response.WriteHeader(http.StatusOK)
	fmt.Fprintf(response, "%s", "{\"status\":\"ok\"}")
}