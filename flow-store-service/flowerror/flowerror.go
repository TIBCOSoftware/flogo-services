package flowerror

type FlowError struct {
	Code    int      `json:"code"`
	Message string `json:"message"`
	Type    string        `json:type`
}