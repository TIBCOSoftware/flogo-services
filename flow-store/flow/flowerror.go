package flow

type FlowError struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
	Type    string `json:type`
}

const (
	InternalError = "Internal DB Error"
)
