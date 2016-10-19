package persistence

import (
	"gopkg.in/redis.v4"
	"flag"
)

var RedisAddr = flag.String("addr", "flogo-redis:6379", "The address of redis server, for example: localhost:6379")

var ReditClient *redis.Client = nil

func NewClient() *redis.Client {
	if ReditClient == nil {
		ReditClient = redis.NewClient(&redis.Options{Addr: *RedisAddr, Password:"", })
	}
	return ReditClient
}

var SNAPSHOT_NAMESPACE = "snapshot:"

var SNAPSHOTS_NAMESPACE = "snapshots:"

var SNAPSHOTS_FLOWS_KEY = "snapshotFlows"

func GetSnapshotMetdata(flowID string) (map[string]string, error) {
	client := NewClient()
	statusComamnd := client.HGet(SNAPSHOT_NAMESPACE + flowID, "status")
	stateComamnd := client.HGet(SNAPSHOT_NAMESPACE + flowID, "state")
	dateCommand := client.HGet(SNAPSHOT_NAMESPACE + flowID, "date")
	idComamnd := client.HGet(SNAPSHOT_NAMESPACE + flowID, "id")

	metadata := make(map[string]string)
	metadata["flowID"] = flowID
	status, err := statusComamnd.Result()
	if err == nil {
		metadata["status"] = status
	}

	state, err := stateComamnd.Result()
	if err == nil {
		metadata["state"] = state
	}

	date, err := dateCommand.Result()
	if err == nil {
		metadata["date"] = date
	}

	id, err := idComamnd.Result()
	if err == nil {
		metadata["id"] = id
	}
	return metadata, err
}