package main

import "github.com/charmbracelet/log"

func Generate() {
	_, err := RunCommand(CmdProps{
		Cmd:       CompileBackend,
		Sync:      true,
		LogBefore: "Compiling backend code",
		Opts:      Opts(WithStdout, WithStderr),
	})
	if err != nil {
		log.Errorf("Error compiling backend code for generate. %v", err)
		return
	}

	_, err = RunCommand(CmdProps{
		Cmd:       GenerateAppBinds,
		Opts:      Opts(WithStdout, WithStderr),
		LogBefore: "Generating bindings.",
		Sync:      true,
	})
	if err != nil {
		log.Errorf("Couldn't generate bindings for some reason. %v", err)
		return
	}
}
