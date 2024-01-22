package main

import (
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

const INIT = "init"
const DEV = "dev"
const BUILD = "build"

func main() {
	cmd := os.Args[1]
	switch cmd {
	case INIT:
		perform(Init)
	case DEV:
		perform(Dev)
	case BUILD:
		perform(Build)
	default:
		panic("invalid usage. proper usage is: coffee <util> <backend-template> <frontend-template>")
		// TODO: add option to print out all available templates.
	}
}
