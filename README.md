# Flogo Services

Flogo services module consists of two Services

## State Service
The Flogo State Service is a service for managing the state of process flows executed on Flogo Engine. This service's primary job is to store process (incremental and full) state for flows that are executed on an engine. This service will also facilitate front-end introspection and debugging of process flows.

## Flow Service
The Flogo Process Service is a service for managing process definitions that are to be executed by the Flogo Engine. This service's primary job is to store process definitions designed in the front-end and provide those definitions to a Flogo Engine on demand.

## Dependencies

The services just depend on docker. You can install docker from [docker website](http://www.docker.com)

## Running the Services

`docker-compose up`

This builds and starts both services in a docker environment. A `redis` image is required and is pulled as part of docker-compose up.

## License
License information goes here. This needs to be updated before we make this repository public.
