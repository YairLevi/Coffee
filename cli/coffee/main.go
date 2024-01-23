package main

import (
	"embed"
	"fmt"
	"github.com/charmbracelet/log"
	"os"
)

//go:embed templates/*
var content embed.FS

func perform(f func() error) {
	if err := f(); err != nil {
		log.Error(err.Error())
		os.Exit(1)
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
		fmt.Println("invalid usage. proper usage is: coffee <util> <backend-template> <frontend-template>")
		return
		// TODO: add option to print out all available templates.
	}
}
