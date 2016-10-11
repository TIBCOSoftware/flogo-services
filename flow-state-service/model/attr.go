package model

type Attr struct {
	ChgType int
	Att Attribute `json:"Attribute"`
}

type Attribute struct {
	Name  string `json:"name"`
	Type  string `json:"type"`
	Value interface{} `json:"value"`
}