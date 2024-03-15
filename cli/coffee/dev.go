package main

import (
	"fmt"
	"github.com/YairLevi/Coffee/cli/coffee/util"
	"github.com/charmbracelet/log"
	"os"
)

func Dev() {
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
		log.Error("Can't go to frontend directory.", "err", err)
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

	devServerCmd, err := RunCommand(CmdProps{
		Cmd:       LaunchDevServer,
		LogBefore: "Starting development server",
		Opts:      Opts(WithStderr),
		Sync:      false,
	})
	if err != nil {
		log.Error("Failed to start dev server.", "err", err)
		return
	}
	defer func() {
		log.Info("Shutting down frontend dev server")
		err = util.StopProcessTree(devServerCmd.Process.Pid)
		if err != nil {
			log.Error("Failed to close the entire process tree of the dev server.", "err", err)
		}
	}()

	err = os.Chdir("..")
	if err != nil {
		log.Error("Can't go back to project directory.", "err", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       LaunchApp,
		Opts:      Opts(WithStderr, WithStdout),
		Sync:      true,
		LogBefore: "Running. application logging below",
	})
	if err != nil {
		fmt.Println("Failed to launch application.", "err", err)
		return
	}
}
