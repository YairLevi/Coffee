package main

import (
	"cli/util"
	"fmt"
	"github.com/charmbracelet/log"
	"os"
	"os/exec"
)

func Dev() error {
	err := os.Chdir("frontend")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}
	log.Info("Installing dependencies")
	err = exec.Command("npm", "install").Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Info("Starting development server")
	devServer := exec.Command("npm", "run", "dev")
	devServer.Stderr = os.Stderr
	err = devServer.Start()
	if err != nil {
		log.Error(err.Error())
		return err
	}

	err = os.Chdir("..")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Info("Compiling Application")
	compile := exec.Command("mvn", "clean", "compile")
	compile.Stderr = os.Stderr
	err = compile.Run()
	if err != nil {
		log.Error(err.Error())
		return err
	}

	log.Info("Running. application logging below")
	log.Info("\n==================================\n")
	cmd := exec.Command("mvn", "exec:java")
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err = cmd.Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}
	log.Info("\n==================================\n")
	log.Info("Shutting down frontend dev server")
	err = util.StopProcessTree(devServer.Process.Pid)
	if err != nil {
		log.Error("Failed to close the entire process tree of the dev server.")
		return err
	}
	return nil
}
