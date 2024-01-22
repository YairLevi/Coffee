package main

import (
	"cli/util"
	"fmt"
	"os"
)

func Build() error {
	err := os.Chdir("frontend")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	buildFront := util.CommandWithLog("npm", "run", "build")
	err = buildFront.Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	err = os.Chdir("..")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	err = util.MoveDirectory("frontend/dist", "src/main/resources/dist")
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	buildApp := util.CommandWithLog("mvn", "clean", "compile", "assembly:single")
	err = buildApp.Run()
	if err != nil {
		fmt.Println(err.Error())
		return err
	}

	return nil
}
