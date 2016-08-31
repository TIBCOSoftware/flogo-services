#!/bin/bash

# @Author - Aditya Wagle
# This is a start script which runs inside the docker container.
#
# The script takes a CLI option to compile. Otherwise it just runs the binaries at /tmp/flow-service

AGENT_PID=1
CONFIG_FILE=../config/server.yml

t_compile(){
  echo "Compile selected"
  cd /tmp/flow-state-service/
  echo "Running Gradle Script"
  cd java
  ./gradlew installDist
  echo "Completed Gradle Script"
}

t_run(){
  echo "Running server"
  cd /tmp/flow-state-service/java/build/install/state-service/bin
  ./state-service server $CONFIG_FILE &
  AGENT_PID=$!
}

t_destroy(){
  echo "Stopping server"
  kill -s SIGTERM $AGENT_PID
  echo 
  exit 0;
}

t_findConfigFile(){
    echo "Trying to find config file at /tmp/server.yml"
    if [ -f /tmp/config/server.yml ]; then
        CONFIG_FILE=/tmp/config/server.yml
    fi
}
t_main(){
    t_findConfigFile
   
    t_run
}

echo "Executing Script to start server"
echo "CLI OPTION IS $1"

if [ "$1" == "compile" ]; then
        t_compile
elif [ "$1" == "compile_and_run" ]; then
        t_compile
        t_main
        trap t_destroy SIGTERM
        wait $AGENT_PID
else
        t_main
        trap t_destroy SIGTERM
        wait $AGENT_PID
fi


