package main

import (
	"cli/util"
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

	log.Println("Compiling Application")
	err = exec.Command("mvn", "clean", "compile").Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Println("Running...")
	cmd := exec.Command("mvn", "exec:java")
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err = cmd.Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	log.Println("Shuting down frontend dev server")
	err = util.StopProcessTree(devServer.Process.Pid)
	if err != nil {
		return fmt.Errorf("Failed to close the entire process tree of the dev server.\n%v", err)
	}
	return nil
}
