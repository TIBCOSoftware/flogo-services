package stateerror

type StateError struct {
	Code    int      `json:"code"`
	Message string `json:"message"`
	Type    string        `json:type`
}


const (
	InternalError = "Internal DB Error"
)