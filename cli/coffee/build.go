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
	_, err := RunCommand(CmdProps{
		Cmd:       CompileBackend,
		Sync:      true,
		Opts:      Opts(WithStderr, WithStdout),
		LogBefore: "Compiling Application",
	})
	if err != nil {
		log.Error("Failed to compile backend code.", "err", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       GenerateAppBinds,
		LogBefore: "Generating type-safe frontend bindings...",
		Sync:      true,
		Opts:      Opts(WithStderr),
	})
	if err != nil {
		log.Errorf("Failed to create bindings. %v", err)
		return
	}

	err = os.Chdir("frontend")
	if err != nil {
		log.Errorf("error: %v", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       InstallFrontendDependencies,
		LogBefore: "Installing dependencies",
		Sync:      true,
	})
	if err != nil {
		log.Error("Failed to download NPM dependencies.", "err", err)
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
	name, err := util.ReadNameFromJSONFile("frontend/package.json")
	if err != nil {
		log.Errorf("Error reading from package.json file. %v", err)
		return
	}
	sourceDir := sourceDirMapping[name]
	err = util.MoveDirectory(sourceDir, "src/main/resources/dist")
	if err != nil {
		log.Errorf("Failed to move dist folder to the resources folder.  %v", err)
		return
	}

	log.Info("Preparing for bundle")
	file, err := os.Create("src/main/resources/__jar__")
	if err != nil {
		log.Errorf("Unexpected error: production flag was not able to set. %v", err)
		return
	}
	file.Close()

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

	log.Info("Post bundle cleanup")
	err = os.Remove("src/main/resources/__jar__")
	if err != nil {
		log.Errorf("Unexpected error: was not able to delete temporary production flag. %v", err)
		return
	}

	log.Info("Done. your JAR is located at `./target")
}
