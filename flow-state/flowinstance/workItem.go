package flowinstance

type WorkItem struct {
	ID       int64 `json:"id"`
	ExecType int64 `json:"execType"`
	TaskId   int64 `json:"taskID"`
	Code int64 `json:"code"`
}

type WorkQueue struct {
	ID       int64 `json:"id"`
	ExecType int64 `json:"execType"`
	Code     int64 `json:"code"`
	TaskId   int64 `json:"taskID"`
}