package main

import (
	"cli/command"
	"embed"
	"os"
)

//go:embed templates/*
var content embed.FS

func main() {
	invoked := os.Args[1]

	switch invoked {
	case command.INIT:
		{
			if err := Init(); err != nil {
				panic(err)
			}
		}

		// TODO: implement other commands.
	case command.DEV:
	case command.BUILD:
	default:
		panic("invalid usage. proper usage is: coffee <command> <backend-template> <frontend-template>")
		// TODO: add option to print out all available templates.
	}
}
