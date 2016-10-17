package cmd

import (
	"flag"
	"github.com/op/go-logging"
)

var log = logging.MustGetLogger("cmd")
var Port = flag.String("p", "9098", "The port of the server")
var RedisAddr = flag.String("addr", "localhost:6379", "The address of redis server, for example: localhost:6379")

func init() {
	log.Info("Starting parse cmd line paramters")
	flag.Parse() // get the arguments from command line
}
