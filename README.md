# Flogo Services

Flogo services module consists of two Services

## State Service
The Flogo State Service is a service for managing the state of process flows executed on Flogo Engine. This service's primary job is to store process (incremental and full) state for flows that are executed on an engine. This service will also facilitate front-end introspection and debugging of process flows.

## Flow Service
The Flogo Process Service is a service for managing process definitions that are to be executed by the Flogo Engine. This service's primary job is to store process definitions designed in the front-end and provide those definitions to a Flogo Engine on demand.

## Dependencies

The services just depend on [redis](http://redis.io/) server. You can start redis server from docker or anyway you like

```bash
docker run -p 6379:6379 --name flogo-redis -d redis
```

## Building the services

The State service and Flow service are both write by Golang, so please make sure you have installed Golang on your machine

* Install gvt tool
```bash
go get -u github.com/FiloSottile/gvt
```
* Clone flogo-services code from github to GOPATH
```bash
cd GOPATH/src/github.com/TIBCOSoftware/
#Please create directory if it is not exist
git clone https://github.com/TIBCOSoftware/flogo-services.git
```
* Restore dependencies
```bash
cd flogo-services/flow-store
gvt restore
cd ../flow-state
gvt restore
#gvt fetch all dependencies and put it into vendor directory
```
* Both state and flow service now are compilable go project
* Build those 2 services
```bash
cd flogo-services/flow-store
go test ./...
go install ./...
cd flogo-services/flow-state
go test ./...
go install ./...
```

## Running the Services
* Under GOPATH/bin folder where generated 2 executable file, fgstore and fgstate
* Start flow and state server
```bash
cd GOPATH/bin
./fgstore -p 9090 -addr localhost:6379
./fgstate -p 9190 -addr localhost:6379

#-p means the port of flow or state service. -addr means redis address
```

## License
flogo-services is licensed under a BSD-type license. See TIBCO LICENSE.txt for license text.
