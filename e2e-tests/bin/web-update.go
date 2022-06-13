package main

import (
	"os"
//	"io/util"

)
var targetDir = "e2e-tests/build/geoserver/"
var srcDir = "e2e-tests/src/main/webapp/"
func copy(path String, fileName String) {
	src := os.Lstat(srcDir + path + fileName)
	if src.IsDir() {

	} else {
		dest := os.Lstat(geoserverDir + path + file.Name())
		if dest.ModTime()
	}
}
func main() {
	webRoot, _ := os.Lstat(".")

	copy ("", webRoot)

}

