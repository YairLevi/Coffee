package main

import (
	"embed"
	"fmt"
	"os"
)

//go:embed templates/*
var content embed.FS

const (
	INIT     = "init"
	DEV      = "dev"
	BUILD    = "build"
	GENERATE = "generate"
)

func main() {
	cmd := os.Args[1]
	switch cmd {
	case INIT:
		Init()
	case GENERATE:
		Generate()
	case DEV:
		Dev()
	case BUILD:
		Build()
	default:
		fmt.Println("invalid usage. undefined command " + cmd)
		return
		// TODO: add option to print out all available templates.
	}
}
