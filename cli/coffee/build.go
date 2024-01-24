package main

import (
	"github.com/YairLevi/Coffee/cli/coffee/util"
	"github.com/charmbracelet/log"
	"os"
)

var sourceDirMapping = map[string]string{
	"react-ts":   "frontend/dist",
	"angular-ts": "frontend/dist/angular-ts/browser",
}

func Build() error {
	err := os.Chdir("frontend")
	if err != nil {
		return err
	}

	log.Info("Making frontend dist folder")
	buildFront := util.CommandWithLog("npm", "run", "build")
	err = buildFront.Run()
	if err != nil {
		return err
	}

	err = os.Chdir("..")
	if err != nil {
		return err
	}

	log.Info("Adding frontend to resources")
	sourceDir := sourceDirMapping[os.Args[2]]
	err = util.MoveDirectory(sourceDir, "src/main/resources/dist")
	if err != nil {
		return err
	}

	log.Info("Bundling to JAR")
	buildApp := util.CommandWithLog("mvn", "clean", "compile", "assembly:single")
	err = buildApp.Run()
	if err != nil {
		return err
	}

	log.Info("Done. your JAR is located at `./target")
	return nil
}
