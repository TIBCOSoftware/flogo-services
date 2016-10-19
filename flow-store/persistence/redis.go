package persistence

import (
	"gopkg.in/redis.v4"
	"flag"
)

var ReditClient = redis.NewClient(&redis.Options{Addr: *RedisAddr, Password:"", })

var RedisAddr = flag.String("addr", "localhost:6379", "The address of redis server, for example: localhost:6379")
