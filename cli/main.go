package main

import (
	"cli/command"
	"embed"
	"os"
)

//go:embed templates/*
var content embed.FS

func perform(f func() error) {
	if err := f(); err != nil {
		panic(err)
	}
}

func main() {
	cmd := os.Args[1]
	switch cmd {
	case command.INIT:
		perform(Init)
	case command.DEV:
		perform(Dev)
	case command.BUILD:
		perform(Build)
	default:
		panic("invalid usage. proper usage is: coffee <command> <backend-template> <frontend-template>")
		// TODO: add option to print out all available templates.
	}
}
