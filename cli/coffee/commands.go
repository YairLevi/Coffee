package main

import (
	"github.com/charmbracelet/log"
	"os"
	"os/exec"
	"strings"
)

const (
	InstallFrontendDependencies = "npm install"
	LaunchDevServer             = "npm run dev"
	BuildFrontend               = "npm run build"
	CompileBackend              = "mvn clean compile"
	BundleApp                   = "mvn clean compile assembly:single"
	LaunchApp                   = "mvn clean exec:java"
	GenerateAppBinds            = "mvn clean exec:java -Dexec.args=\"generate\""
)

type Opt = func(cmd *exec.Cmd)

func WithStdout(cmd *exec.Cmd) {
	cmd.Stdout = os.Stdout
}

func WithStderr(cmd *exec.Cmd) {
	cmd.Stderr = os.Stderr
}

type CmdProps struct {
	Cmd       string
	LogBefore string
	Sync      bool
	Opts      []Opt
}

func Opts(opts ...Opt) []Opt {
	var ropts []Opt
	for _, o := range opts {
		ropts = append(ropts, o)
	}
	return ropts
}

func RunCommand(props CmdProps) (*exec.Cmd, error) {
	words := strings.Split(props.Cmd, " ")
	name := words[0]
	args := words[1:]
	command := exec.Command(name, args...)
	for _, opt := range props.Opts {
		opt(command)
	}
	var err error = nil
	log.Info(props.LogBefore)
	if props.Sync {
		err = command.Run()
	} else {
		err = command.Start()
	}
	return command, err
}
