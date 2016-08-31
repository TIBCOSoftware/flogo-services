# Flogo Services

Flogo services module consists of two Services

## State Service
The Flogo State Service is a service for managing the state of process flows executed on Flogo Engine. This service's primary job is to store process (incremental and full) state for flows that are executed on an engine. This service will also facilitate front-end introspection and debugging of process flows.

## Flow Service
The Flogo Process Service is a service for managing process definitions that are to be executed by the Flogo Engine. This service's primary job is to store process definitions designed in the front-end and provide those definitions to a Flogo Engine on demand.

## Dependencies

The services just depend on docker. You can install docker from [docker website](http://www.docker.com)

## Building the services

This will generate a docker compose start up shell script using the desired docker image tags. 
if the BUILD_RELEASE_TAG is empty the 'latest' tag is used by default e.g.
```
#!/bin/bash
script_root=$(dirname "${BASH_SOURCE}")
export BUILD_RELEASE_TAG=0.2.0
export DOCKER_REGISTRY=reldocker.tibco.com/
docker-compose -f ${script_root}/docker-compose.yml up
docker-compose rm -f
```
### Using private docker registry example
`BUILD_RELEASE_TAG=1.0.0 DOCKER_REGISTRY=localhost:5000/ ./build-flogo-services.sh`

## Running the Services

### Using private docker registry example
`BUILD_RELEASE_TAG=1.0.0 DOCKER_REGISTRY=localhost:5000/ ./docker-compose-start.sh`

This starts both services in a docker environment. A `redis` image is required and is pulled as part of docker-compose up.

## License
License information goes here. This needs to be updated before we make this repository public.
