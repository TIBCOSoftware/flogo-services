package flowinstance

type Change struct {
	Name  string      `json:"name"`
	Type  string      `json:"type"`
	Value interface{} `json:"value"`
}

type TdChange struct {
	ChgType  int64       `json:"ChgType"`
	ID       interface{} `json:"ID"`
	TaskData TaskData    `json:"TaskData"`
}

type LdChange struct {
	ChgType  int64    `json:"ChgType"`
	ID       int64    `json:"ID"`
	LinkData LinkData `json:"LinkData"`
}

type WqChange struct {
	ChgType int64       `json:"ChgType"`
	ID      interface{} `json:"ID"`
	Wtem    WorkItem    `json:"WorkItem"`
}

type TaskData struct {
	State  int64       `json:"state"`
	Done   bool        `json:"done"`
	Attrs  interface{} `json:"attrs"`
	TaskId interface{} `json:"taskId"`
}

type LinkData struct {
	State  int64       `json:"state"`
	Attrs  interface{} `json:"attrs"`
	LinkId int64       `json:"linkId"`
}

type ChangeObj struct {
	ID                   int64                  `json:"id"`
	Sequence             interface{}            `json:"sequence"`
	Change               Change                 `json:"change"`
	AdditionalProperties map[string]interface{} `json:"additionalProperties"`
}
