package persistence

import (
	"gopkg.in/redis.v4"
	"flag"
)

var ReditClient *redis.Client = nil

func NewClient() *redis.Client {
	if ReditClient == nil {
		ReditClient = redis.NewClient(&redis.Options{Addr: *RedisAddr, Password:"", })
	}
	return ReditClient
}

var RedisAddr = flag.String("addr", "flogo-redis:6379", "The address of redis server, for example: localhost:6379")
