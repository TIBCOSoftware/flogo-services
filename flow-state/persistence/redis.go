package persistence

import (
	"gopkg.in/redis.v4"
	"flag"
)

var RedisAddr = flag.String("addr", "localhost:6379", "The address of redis server, for example: localhost:6379")
var ReditClient = redis.NewClient(&redis.Options{Addr: *RedisAddr, Password: ""})

var SNAPSHOT_NAMESPACE = "snapshot:"

var SNAPSHOTS_NAMESPACE = "snapshots:"

var SNAPSHOTS_FLOWS_KEY = "snapshotFlows"

func GetSnapshotMetdata(flowID string) (map[string]string, error) {
	statusComamnd := ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "status")
	stateComamnd := ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "state")
	dateCommand := ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "date")
	idComamnd := ReditClient.HGet(SNAPSHOT_NAMESPACE + flowID, "id")

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


func init() {
	flag.Parse() // get the arguments from command line
}