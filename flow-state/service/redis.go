package service

import (
	"gopkg.in/redis.v4"
	"github.com/TIBCOSoftware/flogo-services/flow-state/cmd"
)

var ReditClient = redis.NewClient(&redis.Options{Addr: *cmd.RedisAddr, Password: ""})