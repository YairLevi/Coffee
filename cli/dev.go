package main

import (
	"cli/command"
	"fmt"
	"log"
	"os"
	"os/exec"
)

func Dev() error {
	err := os.Chdir("frontend")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}
	log.Println("Installed dependencies")
	err = exec.Command("npm", "install").Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Println("Starting development server")
	devServer := exec.Command("npm", "run", "dev")
	devServer.Stderr = os.Stderr
	err = devServer.Start()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	err = os.Chdir("..")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Println("Running application")
	err = exec.Command("mvn", "clean", "compile", "exec:java").Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	err = command.StopProcessTree(devServer.Process.Pid)
	if err != nil {
		return fmt.Errorf("Failed to close the entire process tree of the dev server.\n%v", err)
	}
	return nil
}
