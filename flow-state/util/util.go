package util

import (
	"strings"
	"strconv"
	"fmt"
)

func Contains(s []int64, e int64) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}

func InterfaceContains(s []interface{}, e interface{}) bool {
	for _, a := range s {
		if a == e {
			return true
		}
	}
	return false
}

func StringContains(s []string, e string) bool {
	for _, a := range s {
		if strings.EqualFold(a, e) {
			return true
		}
	}
	return false
}

func IsRootTask(taskId interface{}) bool {
	strId := convertInterfaceToString(taskId)
	if strId == "1" || strId == "root"{
		return true
	}
	return false
}

func GetRootId(taskId interface{}) interface{}{
	if taskId == nil {
		panic("Invalid nil taskId found")
	}
	switch taskId.(type) {
	case string:
		return "root"
	case float64:
		return  1
	default:
		panic(fmt.Sprintf("Error parsing Task with Id '%v', invalid type '%T'", taskId, taskId))
	}
}

//convertInterfaceToString will identify whether the interface is int or string and return a string in any case
func convertInterfaceToString(m interface{}) string {
	if m == nil {
		panic("Invalid nil activity id found")
	}
	switch m.(type) {
	case string:
		return m.(string)
	case float64:
		return strconv.Itoa(int(m.(float64)))
	default:
		panic(fmt.Sprintf("Error parsing Task with Id '%v', invalid type '%T'", m, m))
	}
}
