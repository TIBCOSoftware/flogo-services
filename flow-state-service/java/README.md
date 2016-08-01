# Flogo State Service 

The **Flogo State Service** is a service for managing the state of process flows executed on **Flogo Engine**.  This service's primary job is to store process (incremental and full) state for flows that are executed on an engine.  This service will also facilitate front-end introspection and debugging of process flows.

    GET     /flows - Retreives list of all flows with there status
    DELETE  /flows/{flowID} - Deletes flow entry
    GET     /flows/{flowID}/status - Retreives flow status
    GET     /instances - Depreciated
    POST    /instances/snapshot - Depreciated 
    GET     /instances/snapshots - Depreciated 
    POST    /instances/steps - Depreciated
    DELETE  /instances/{flowID} - Depreciated
    GET     /instances/{flowID}/metadata - Depreciated
    GET     /instances/{flowID}/snapshot/{id} - Depreciated
    GET     /instances/{flowID}/status - Depreciated
    GET     /ping - Retrieves status of Service
    GET     /snapshots - Retreives all snapshots
    POST    /snapshots/snapshot - Saves snapshot for flow
    DELETE  /snapshots/{flowID} - Deletes all related snapshot data for flow
    GET     /snapshots/{id} - Retreives individual snapshot for flow
    GET     /snapshots/{id}/metadata - Retreives metadata for individual snapshot
    GET     /steps - Retreives all steps
    POST    /steps - Saves step for flow
    GET     /steps/flow/{flowid}/stepdata - Retrieves all steps for flow
    GET     /steps/{flowid}/stepids - Retreives all step ids for flow
    DELETE  /steps/{flowid} - Deletes all step data for flow
    GET     /steps/{id}/rollup - Retreives rollup for flow
    GET     /steps/{id}/stepdata - Retreives step data for flow

The following are the REST API's along with sample output.

GET     /flows (com.tibco.flogo.ss.resource.FlowResource)
	
	curl -X "GET" http://localhost:9000/flows
	http://localhost:9000/flows
	
	[{
		"id": "afb44dae489cf7c21ab33ba1eecea306",
		"status": "100"
	}, {
		"id": "0414483e99633add49607bf2dbc63c01",
		"status": "500"
	}, {
		"id": "7af07346f8dc4d3a8af9e2228152e984",
		"status": "500"
	}]
	
DELETE  /flows/{flowID} (com.tibco.flogo.ss.resource.FlowResource)
	
	curl -X "DELETE" http://localhost:9000/flows/afb44dae489cf7c21ab33ba1eecea306
	
		
GET     /flows/{flowID}/status (com.tibco.flogo.ss.resource.FlowResource)
	
	curl -X "GET" http://localhost:9000/flows/ec5bf3a3b6f3298efcd8e04306b36911/status
	
	{
		"id": "ec5bf3a3b6f3298efcd8e04306b36911",
		"status": "500"
	}
	
GET     /instances (com.tibco.flogo.ss.resource.InstanceResource)

	Depreciated - replaced by "/flows"
	
POST    /instances/snapshot (com.tibco.flogo.ss.resource.InstanceResource)

	Depreciated - replaced by "snapshots/snapshot"
	
GET     /instances/snapshots (com.tibco.flogo.ss.resource.InstanceResource)
	
	Depreciated - replaced by "/snapshots"
	
POST    /instances/steps (com.tibco.flogo.ss.resource.InstanceResource)
	
	Depreciated - replaced by "/steps"
	
DELETE  /instances/{flowID} (com.tibco.flogo.ss.resource.InstanceResource)
	
	Depreciated - replaced by "/flows/{flowID}"
	
GET     /instances/{flowID}/metadata (com.tibco.flogo.ss.resource.InstanceResource)
	
	Depreciated - replaced by "snapshots/{id}/metadata"
	
	
GET     /instances/{flowID}/snapshot/{id} (com.tibco.flogo.ss.resource.InstanceResource)
	
	Depreciated - replaced by "snapshots/{id}"
	
GET     /instances/{flowID}/status (com.tibco.flogo.ss.resource.InstanceResource)

	Depreciated - replaced by "/flows/{flowID}/status"
	
GET     /ping (com.tibco.flogo.ss.resource.PingResource)
	
	{
		"status": "ok"
	}

GET     /snapshots (com.tibco.flogo.ss.resource.SnapshotResource)
	
	http://localhost:9000/snapshots
	curl -X "GET" http://localhost:9000/snapshots
	
	
	[{
	"date": "Fri Jul 29 11:42:11 EDT 2016",
	"id": "3",
	"state": "0",
	"flowID": "0414483e99633add49607bf2dbc63c01:3",
	"status": "100"
}, {
	"date": "Thu Jul 28 14:08:23 EDT 2016",
	"id": "2",
	"state": "0",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306:2",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:41 EDT 2016",
	"id": "1",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:1",
	"status": "100"
}, {
	"date": "Mon Aug 01 10:09:07 EDT 2016",
	"id": "0",
	"state": "0",
	"flowID": "ec5bf3a3b6f3298efcd8e04306b36911:0",
	"status": "100"
}, {
	"date": "Fri Jul 29 11:42:12 EDT 2016",
	"id": "4",
	"state": "0",
	"flowID": "0414483e99633add49607bf2dbc63c01:4",
	"status": "500"
}, {
	"date": "Thu Jul 28 14:08:24 EDT 2016",
	"id": "3",
	"state": "0",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306:3",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:42 EDT 2016",
	"id": "2",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:2",
	"status": "100"
}, {
	"date": "Mon Aug 01 10:09:14 EDT 2016",
	"id": "1",
	"state": "0",
	"flowID": "ec5bf3a3b6f3298efcd8e04306b36911:1",
	"status": "100"
}, {
	"date": "Thu Jul 28 14:08:24 EDT 2016",
	"id": "4",
	"state": "0",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306:4",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:43 EDT 2016",
	"id": "3",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:3",
	"status": "100"
}, {
	"date": "Mon Aug 01 10:09:15 EDT 2016",
	"id": "2",
	"state": "0",
	"flowID": "ec5bf3a3b6f3298efcd8e04306b36911:2",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:44 EDT 2016",
	"id": "4",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:4",
	"status": "100"
}, {
	"date": "Mon Aug 01 10:09:15 EDT 2016",
	"id": "3",
	"state": "0",
	"flowID": "ec5bf3a3b6f3298efcd8e04306b36911:3",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:45 EDT 2016",
	"id": "5",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:5",
	"status": "100"
}, {
	"date": "Mon Aug 01 10:09:16 EDT 2016",
	"id": "4",
	"state": "0",
	"flowID": "ec5bf3a3b6f3298efcd8e04306b36911:4",
	"status": "500"
}, {
	"date": "Fri Jul 29 11:42:05 EDT 2016",
	"id": "0",
	"state": "0",
	"flowID": "0414483e99633add49607bf2dbc63c01:0",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:46 EDT 2016",
	"id": "6",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:6",
	"status": "500"
}, {
	"date": "Fri Jul 29 11:42:10 EDT 2016",
	"id": "1",
	"state": "0",
	"flowID": "0414483e99633add49607bf2dbc63c01:1",
	"status": "100"
}, {
	"date": "Thu Jul 28 14:08:18 EDT 2016",
	"id": "0",
	"state": "0",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306:0",
	"status": "100"
}, {
	"date": "Fri Jul 29 11:42:11 EDT 2016",
	"id": "2",
	"state": "0",
	"flowID": "0414483e99633add49607bf2dbc63c01:2",
	"status": "100"
}, {
	"date": "Thu Jul 28 14:08:23 EDT 2016",
	"id": "1",
	"state": "0",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306:1",
	"status": "100"
}, {
	"date": "Tue Jul 26 10:04:40 EDT 2016",
	"id": "0",
	"state": "0",
	"flowID": "7af07346f8dc4d3a8af9e2228152e984:0",
	"status": "100"
}]
	
POST    /snapshots/snapshot (com.tibco.flogo.ss.resource.SnapshotResource)
	
DELETE  /snapshots/{flowID} (com.tibco.flogo.ss.resource.SnapshotResource)
	
	curl -X "DELETE" http://localhost:9000/snapshots/ec5bf3a3b6f3298efcd8e04306b36911
	
GET     /snapshots/{id} (com.tibco.flogo.ss.resource.SnapshotResource)
	
	curl -X "GET" http://localhost:9000/snapshots/0414483e99633add49607bf2dbc63c01:4
	http://localhost:9000/instances/0414483e99633add49607bf2dbc63c01/snapshot/4
	
	{
		"date": "Fri Jul 29 11:42:12 EDT 2016",
		"state": "0",
		"id": "4",
		"snapshotData": "{\"id\":\"0414483e99633add49607bf2dbc63c01\",\"status\":500,\"state\":0,\"flowUri\":\"embedded://test\",\"attrs\":[{\"name\":\"{A3.result}\",\"type\":\"object\",\"value\":{\"id\":222,\"photoUrls\":[],\"tags\":[]}},{\"name\":\"{A4.message}\",\"type\":\"string\",\"value\":\"'' - FlowInstanceID [0414483e99633add49607bf2dbc63c01], Flow [test:no branch:query a pet with log], Task [log pet]\"},{\"name\":\"{T.params}\",\"type\":\"params\",\"value\":{}},{\"name\":\"{T.pathParams}\",\"type\":\"params\",\"value\":{}},{\"name\":\"{T.queryParams}\",\"type\":\"params\",\"value\":{}},{\"name\":\"{T.content}\",\"type\":\"object\",\"value\":null},{\"name\":\"{A2.message}\",\"type\":\"string\",\"value\":\"'logging...' - FlowInstanceID [0414483e99633add49607bf2dbc63c01], Flow [test:no branch:query a pet with log], Task [log start]\"}],\"workQueue\":[],\"rootTaskEnv\":{\"id\":1,\"taskId\":1,\"taskDatas\":[],\"linkDatas\":[]}}",
		"flowID": "0414483e99633add49607bf2dbc63c01",
		"status": "500"
	}
	
GET     /snapshots/{id}/metadata (com.tibco.flogo.ss.resource.SnapshotResource)

	http://localhost:9000/snapshots/0414483e99633add49607bf2dbc63c01:1/metadata
	curl -X "GET" http://localhost:9000/snapshots/0414483e99633add49607bf2dbc63c01:1/metadata
	
	{
		"date": "Fri Jul 29 11:42:10 EDT 2016",
		"id": "1",
		"state": "0",
		"flowID": "0414483e99633add49607bf2dbc63c01:1",
		"status": "100"
	}
	
	
	
	
GET     /steps (com.tibco.flogo.ss.resource.StepResource)
	
	curl -X "GET" http://localhost:9000/steps
	http://localhost:9000/steps (steps to every flow)

	[{
	"date": "Tue Jul 12 10:26:54 EDT 2016",
	"stepData": "{\"status\":0,\"state\":0,\"wqChanges\":[{\"ChgType\":3,\"ID\":6,\"WorkItem\":{\"id\":6,\"execType\":10,\"code\":0,\"taskID\":6}}],\"tdChanges\":[{\"ChgType\":3,\"ID\":6,\"TaskData\":null}]}",
	"state": "0",
	"id": "5",
	"flowID": "5550197c44e9072a6dd9c8e7a0737311",
	"status": "100"
}, {
	"date": "Tue Jul 12 10:26:54 EDT 2016",
	"stepData": "{\"status\":0,\"state\":0,\"wqChanges\":[{\"ChgType\":3,\"ID\":1,\"WorkItem\":{\"id\":1,\"execType\":10,\"code\":0,\"taskID\":1}},{\"ChgType\":1,\"ID\":2,\"WorkItem\":{\"id\":2,\"execType\":10,\"code\":0,\"taskID\":2}}],\"tdChanges\":[{\"ChgType\":2,\"ID\":1,\"TaskData\":{\"taskId\":1,\"state\":30,\"attrs\":[]}},{\"ChgType\":2,\"ID\":2,\"TaskData\":{\"taskId\":2,\"state\":20,\"attrs\":[]}}]}",
	"id": "1",
	"state": "0",
	"flowID": "5550197c44e9072a6dd9c8e7a0737311",
	"status": "100"
}, ...
	
POST    /steps (com.tibco.flogo.ss.resource.StepResource)

GET     /steps/{flowid}/stepdata (com.tibco.flogo.ss.resource.StepResource)
	
	http://localhost:9000/steps/flow/5550197c44e9072a6dd9c8e7a0737311/stepdata (steps to one flow)
	curl -X "GET" http://localhost:9000/steps/flow/5550197c44e9072a6dd9c8e7a0737311/stepdata

[{
	"date": "Tue Jul 12 10:26:54 EDT 2016",
	"stepData": "{\"status\":0,\"state\":0,\"wqChanges\":[{\"ChgType\":3,\"ID\":1,\"WorkItem\":{\"id\":1,\"execType\":10,\"code\":0,\"taskID\":1}},{\"ChgType\":1,\"ID\":2,\"WorkItem\":{\"id\":2,\"execType\":10,\"code\":0,\"taskID\":2}}],\"tdChanges\":[{\"ChgType\":2,\"ID\":1,\"TaskData\":{\"taskId\":1,\"state\":30,\"attrs\":[]}},{\"ChgType\":2,\"ID\":2,\"TaskData\":{\"taskId\":2,\"state\":20,\"attrs\":[]}}]}",
	"id": "1",
	"state": "0",
	"flowID": "5550197c44e9072a6dd9c8e7a0737311",
	"status": "100"
}, {
	"date": "Tue Jul 12 10:26:54 EDT 2016",
	"stepData": "{\"status\":0,\"state\":0,\"wqChanges\":[{\"ChgType\":3,\"ID\":2,\"WorkItem\":{\"id\":2,\"execType\":10,\"code\":0,\"taskID\":2}},{\"ChgType\":1,\"ID\":3,\"WorkItem\":{\"id\":3,\"execType\":10,\"code\":0,\"taskID\":3}}],\"tdChanges\":[{\"ChgType\":3,\"ID\":2,\"TaskData\":null},{\"ChgType\":2,\"ID\":3,\"TaskData\":{\"taskId\":3,\"state\":20,\"attrs\":[]}}]}",
	"id": "2",
	"state": "0",
	"flowID": "5550197c44e9072a6dd9c8e7a0737311",
	"status": "100"
},  ...
	
    DELETE  /steps/{id} (com.tibco.flogo.ss.resource.StepResource)
	
GET     /steps/{id}/rollup (com.tibco.flogo.ss.resource.StepResource)
	
	curl -X "GET" http://localhost:9000/steps/afb44dae489cf7c21ab33ba1eecea306:4/rollup
	
	{
	"attrs": [{
		"name": "{T.content}",
		"type": "object",
		"value": null
	}, {
		"name": "{T.pathParams}",
		"type": "params",
		"value": {}
	}, {
		"name": "{A3.result}",
		"type": "object",
		"value": {
			"code": 500,
			"message": "something bad happened",
			"type": "unknown"
		}
	}, {
		"name": "{T.params}",
		"type": "params",
		"value": {}
	}, {
		"name": "{A2.message}",
		"type": "string",
		"value": "'Start logging...' - FlowInstanceID [afb44dae489cf7c21ab33ba1eecea306], Flow [Try to find pet], Task [log start]"
	}, {
		"name": "{T.queryParams}",
		"type": "params",
		"value": {}
	}],
	"id": 4,
	"status": 100,
	"state": 0,
	"flowUri": "afb44dae489cf7c21ab33ba1eecea306",
	"snapshot": {
		"id": null,
		"status": null,
		"state": null,
		"attrs": [{
			"name": "{T.content}",
			"type": "object",
			"value": null
		}, {
			"name": "{T.pathParams}",
			"type": "params",
			"value": {}
		}, {
			"name": "{A3.result}",
			"type": "object",
			"value": {
				"code": 500,
				"message": "something bad happened",
				"type": "unknown"
			}
		}, {
			"name": "{T.params}",
			"type": "params",
			"value": {}
		}, {
			"name": "{A2.message}",
			"type": "string",
			"value": "'Start logging...' - FlowInstanceID [afb44dae489cf7c21ab33ba1eecea306], Flow [Try to find pet], Task [log start]"
		}, {
			"name": "{T.queryParams}",
			"type": "params",
			"value": {}
		}],
		"flowUri": null,
		"workQueue": [],
		"rootTaskEnv": {
			"id": 1,
			"taskId": 1,
			"taskDatas": [{
				"state": 0,
				"done": null,
				"attrs": [],
				"taskId": 1
			}],
			"linkDatas": [{
				"state": 0,
				"attrs": null,
				"linkId": 2
			}, {
				"state": 0,
				"attrs": null,
				"linkId": 3
			}]
		}
	}
}
	
    GET     /steps/{id}/stepdata (com.tibco.flogo.ss.resource.StepResource)
	
	curl -X "GET" http://localhost:9000/steps/afb44dae489cf7c21ab33ba1eecea306:4/stepdata
	
	{
	"date": "Thu Jul 28 14:08:24 EDT 2016",
	"stepData": "{\"status\":0,\"state\":0,\"attrs\":[{\"ChgType\":1,\"Attribute\":{\"name\":\"{A3.result}\",\"type\":\"object\",\"value\":{\"code\":500,\"message\":\"something bad happened\",\"type\":\"unknown\"}}}],\"wqChanges\":[{\"ChgType\":3,\"ID\":3,\"WorkItem\":{\"id\":3,\"execType\":10,\"code\":0,\"taskID\":3}}],\"tdChanges\":[{\"ChgType\":3,\"ID\":3,\"TaskData\":null}],\"ldChanges\":[{\"ChgType\":1,\"ID\":2,\"LinkData\":{\"linkId\":2,\"state\":0}},{\"ChgType\":1,\"ID\":3,\"LinkData\":{\"linkId\":3,\"state\":0}},{\"ChgType\":3,\"ID\":1,\"LinkData\":null}]}",
	"state": "0",
	"id": "3",
	"flowID": "afb44dae489cf7c21ab33ba1eecea306",
	"status": "100"
}

	
    GET     /steps/{id}/stepids (com.tibco.flogo.ss.resource.StepResource)
	
	["step:afb44dae489cf7c21ab33ba1eecea306:0", "step:afb44dae489cf7c21ab33ba1eecea306:1", "step:afb44dae489cf7c21ab33ba1eecea306:2", "step:afb44dae489cf7c21ab33ba1eecea306:3", "step:afb44dae489cf7c21ab33ba1eecea306:4"]