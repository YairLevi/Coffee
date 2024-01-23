package main

//
//import (
//	"fmt"
//	"os"
//	"path/filepath"
//	"strings"
//)
//
//func PrepHidden() error {
//	const templatePath = "templates/"
//	return filepath.Walk(templatePath, func(path string, info os.FileInfo, err error) error {
//		if err != nil {
//			return err
//		}
//
//		// Check if the file or directory name starts with a dot "."
//		if strings.HasPrefix(info.Name(), ".") {
//			// Generate the new name by removing the dot and adding a double underscore
//			newName := strings.Replace(info.Name(), ".", "", 1) + "__"
//
//			// Construct the new path
//			newPath := filepath.Join(filepath.Dir(path), newName)
//
//			// Rename the file or directory
//			err := os.Rename(path, newPath)
//			if err != nil {
//				return err
//			}
//
//			fmt.Printf("Renamed %s to %s\n", path, newPath)
//		}
//
//		return nil
//	})
//}
//
//func main() {
//	if err := PrepHidden(); err != nil {
//		panic(err)
//	}
//}
