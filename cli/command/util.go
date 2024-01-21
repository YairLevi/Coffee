package command

import (
	"fmt"
	"os/exec"
)

const INIT = "init"
const DEV = "dev"
const BUILD = "build"

func StopProcessTree(pid int) error {
	cmd := exec.Command("taskkill", "/F", "/T", "/PID", fmt.Sprint(pid))
	return cmd.Run()
}
