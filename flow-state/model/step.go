package model

import (
	"strings"
	"github.com/TIBCOSoftware/flogo-services/flow-state/util"
)

type StepInfo struct {
	FlowID   string `json:"flowID"`
	ID       int64 `json:"id"`
	StepData StepData `json:"stepData"`
	Status   int64 `json:"status"`
	State    int64 `json:"state"`
	Date     string `json:"date"`
}

type StepData struct {
	Status    int64 `json:"status"`
	State     int64 `json:"state"`
	WqChanges []WqChange `json:"wqChanges"`
	TdChanges []TdChange `json:"tdChanges"`
	LdChanges []LdChange `json:"ldChanges"`
	Attrs     []Attr `json:"attrs"`
}

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

type Attr struct {
	ChgType int
	Att Attribute `json:"Attribute"`
}

type Attribute struct {
	Name  string `json:"name"`
	Type  string `json:"type"`
	Value interface{} `json:"value"`
}

type RollUpObj struct {
	ID                 int64 `json:"id"`
	Status             int64 `json:"status"`
	State              int64 `json:"state"`
	FlowURI            string `json:"flowUri"`
	Snapshot           SnapshotData `json:"snapshot"`
	IgnoredTaskIds     []int64
	IgnoredLinkds      []int64
	IgnoredWorkitemIds []int64
	IgnoredAttrs       []string
}

//func (s StepInfo) Len() int {
//	return len(s.ID)
//}
//func (s StepInfo) Swap(i, j int) {
//	s.ID[i], s.ID[j] = s.ID[j], s.ID[i]
//}
//func (s StepInfo) Less(i, j int) bool {
//	return len(s.ID[i]) < len(s.ID[j])
//}

func (r RollUpObj) UpdateWorkQueueItem(workitem WorkItem) {
	if (!util.Contains(r.IgnoredWorkitemIds, workitem.ID)) {
		results := append(r.IgnoredWorkitemIds, workitem.ID);
		r.IgnoredWorkitemIds = results
		workqs := append(r.Snapshot.WorkQueue, workitem)
		r.Snapshot.WorkQueue = workqs
	}
}
func (r RollUpObj) AddWorkQueueItem(workitem WorkItem) {
	if (!util.Contains(r.IgnoredWorkitemIds, workitem.ID)) {
		workq := append(r.Snapshot.WorkQueue, workitem)
		r.Snapshot.WorkQueue = workq
	}
}

func (r RollUpObj) RemoveWorkQueueItem(workitem WorkItem) {
	ids := append(r.IgnoredWorkitemIds, workitem.ID)
	r.IgnoredWorkitemIds = ids
}

func (r RollUpObj) AddTask(taskData TaskData) {
	if taskData != (TaskData{}) {
		if !util.Contains(r.IgnoredTaskIds, taskData.TaskId) {
			taskDatas := append(r.Snapshot.RootTaskEnv.TaskDatas, taskData)
			r.Snapshot.RootTaskEnv.TaskDatas = taskDatas
		}
	}
}

func (r RollUpObj) RemoveTask(taskId int64) {
	tasks := r.Snapshot.RootTaskEnv.TaskDatas
	for index, task := range tasks {
		if task.TaskId == taskId {
			result := []TaskData{}
			result = append(result, tasks[0:index]...)
			result = append(result, tasks[index + 1:]...)
			r.Snapshot.RootTaskEnv.TaskDatas = result
		}
	}
}

func (r RollUpObj) UpdateTask(taskData TaskData) {
	if taskData != (TaskData{}) {
		if !util.Contains(r.IgnoredTaskIds, taskData.TaskId) {
			ids := append(r.IgnoredTaskIds, taskData.TaskId)
			r.IgnoredTaskIds = ids
			r.RemoveTask(taskData.TaskId)
			taskdatas := append(r.Snapshot.RootTaskEnv.TaskDatas, taskData)
			r.Snapshot.RootTaskEnv.TaskDatas = taskdatas
		}
	}
}

func (r RollUpObj) AddLink(linkData LinkData) {
	if linkData != (LinkData{}) {
		if !util.Contains(r.IgnoredLinkds, linkData.LinkId) {
			lind := append(r.Snapshot.RootTaskEnv.LinkDatas, linkData)
			r.Snapshot.RootTaskEnv.LinkDatas = lind
		}
	}
}

func (r RollUpObj) RemoveLink(linkId int64) {
	links := r.Snapshot.RootTaskEnv.LinkDatas
	for index, task := range links {
		if linkId == task.LinkId {
			result := []LinkData{}
			result = append(result, links[0:index]...)
			result = append(result, links[index + 1:]...)
			r.Snapshot.RootTaskEnv.LinkDatas = result
		}
	}
}

func (r RollUpObj) UpdateLink(linkData LinkData) {
	if linkData != (LinkData{}) {
		if !util.Contains(r.IgnoredLinkds, linkData.LinkId) {
			ids := append(r.IgnoredTaskIds, linkData.LinkId)
			r.IgnoredTaskIds = ids
			r.RemoveTask(linkData.LinkId)
			linds := append(r.Snapshot.RootTaskEnv.LinkDatas, linkData)
			r.Snapshot.RootTaskEnv.LinkDatas = linds
		}
	}
}

func (r RollUpObj) AddAttr(attr Attribute) {
	if attr != (Attribute{}) {
		if !util.StringContains(r.IgnoredAttrs, attr.Name) {
			results := append(r.Snapshot.Attrs, attr)
			r.Snapshot.Attrs = results
		}
	}
}

func (r RollUpObj) RemoveAttr(attr Attribute) {
	if attr != (Attribute{}) {
		attrs := r.Snapshot.Attrs
		for index, task := range attrs {
			if strings.EqualFold(task.Name, attr.Name) {
				result := []Attribute{}
				result = append(result, attrs[0:index]...)
				result = append(result, attrs[index + 1:]...)
				r.Snapshot.Attrs = result
			}
		}
		results := append(r.IgnoredAttrs, attr.Name)
		r.IgnoredAttrs = results
	}
}

func (r RollUpObj) UpdateAttr(attr Attribute) {
	if attr != (Attribute{}) {
		results := append(r.IgnoredAttrs, attr.Name)
		r.IgnoredAttrs = results
		snapResults := append(r.Snapshot.Attrs, attr)
		r.Snapshot.Attrs = snapResults
	}
}