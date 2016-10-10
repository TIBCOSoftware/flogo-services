package model

import "encoding/json"

type FlowInfo struct {
	Id          string `json:"id"`
	Name        string `json:"name"`
	Description string `json:"description"`
	Flow        string `json:"flow"`
}

func (fi *FlowInfo) UnmarshalJSON(data []byte) error {
	ser := &struct {
		Id          string `json:"id"`
		Name        string `json:"name"`
		Description string `json:"description"`
		Flow        interface{} `json:"flow"`
	}{}

	if err := json.Unmarshal(data, ser); err != nil {
		return err
	}

	fi.Id = ser.Id
	fi.Name = ser.Name
	fi.Description = ser.Description

	flow, err := json.Marshal(ser.Flow)
	if err != nil {
		return err
	}else {
		fi.Flow = string(flow)
	}

	return nil
}
