package model

type Snapshot struct {
	ID           int64 `json:"id"`
	FlowID       string `json:"flowID"`
	State        int64 `json:"state"`
	Status       int64 `json:"status"`
	SnapshotData SnapshotData `json:"SnapshotData"`
}

type SnapshotData struct {
	ID          string `json:"id"`
	State       int64 `json:"state"`
	Status      int64 `json:"status"`
	Attrs       []Attribute `json:"attrs"`
	FlowURI     string `json:"flowUri"`
	WorkQueue   []WorkItem `json:"workQueue"`
	RootTaskEnv RootTaskEnv `json:"rootTaskEnv"`
}

type RootTaskEnv struct {
	ID       int64 `json:"id"`
	TaskId   int64 `json:"taskId"`
	TaskDatas []TaskData `json:"taskDatas"`
	LinkDatas []LinkData `json:"linkDatas"`
}

type SnapshotInfo struct {
	ID           int64 `json:"id"`
	FlowID       string `json:"flowID"`
	State        int64 `json:"state"`
	Status       int64 `json:"status"`
	Snapshot     SnapshotData `json:"snapshot"`
	Date         string `json:"date"`
	SnapshotData SnapshotData `json:"snapshotData"`
}