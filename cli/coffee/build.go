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

func Build() {
	if len(os.Args) < 3 {
		log.Error("specify which ui template to build for (will change later)")
		return
	}
	err := os.Chdir("frontend")
	if err != nil {
		log.Errorf("error: %v", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       BuildFrontend,
		Sync:      true,
		LogBefore: "Making frontend dist folder",
		Opts:      Opts(WithStdout, WithStderr),
	})
	if err != nil {
		log.Errorf("Failed to build frontend: %v", err)
		return
	}

	err = os.Chdir("..")
	if err != nil {
		log.Errorf("error moving directory: %v", err)
		return
	}

	log.Info("Adding frontend to resources")
	sourceDir := sourceDirMapping[os.Args[2]]
	err = util.MoveDirectory(sourceDir, "src/main/resources/dist")
	if err != nil {
		log.Errorf("Failed to move dist folder to the resources folder.  %v", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       BundleApp,
		LogBefore: "Bundling to JAR",
		Sync:      true,
		Opts:      Opts(WithStdout, WithStderr),
	})
	if err != nil {
		log.Errorf("Failed to build app into JAR. %v", err)
		return
	}
	log.Info("Done. your JAR is located at `./target")
}
