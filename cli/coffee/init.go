package main

import (
	"errors"
	"fmt"
	"github.com/YairLevi/Coffee/cli/coffee/util"
	"github.com/charmbracelet/log"
	"io"
	"io/fs"
	"os"
	"path/filepath"
	"strings"
)

func printAvailableTemplates() {
	uiTemplatePath := "templates/ui"
	entries, err := fs.ReadDir(content, uiTemplatePath)
	if err != nil {
		log.Error("unexpected internal error.", "err", err)
		os.Exit(1)
	}
	log.Info("Available frontend templates:")
	for _, entry := range entries {
		log.Info("\t" + entry.Name())
	}

	backendTemplatePath := "templates/backend"
	entries, err = fs.ReadDir(content, backendTemplatePath)
	if err != nil {
		log.Error("unexpected internal error.", "err", err)
		os.Exit(1)
	}
	log.Info("Available backend templates:")
	for _, entry := range entries {
		log.Info("\t" + entry.Name())
	}
}

func Init() error {
	if len(os.Args) < 4 {
		return util.LogAndReturn(
			log.Error,
			"not enough arguments.\nproper usage is: coffee <util> <backend-template> <frontend-template>",
			errors.New("usage error"),
		)
	}
	var (
		uiTemplatePath      = "templates/ui/"
		backendTemplatePath = "templates/backend/"
		backend             = os.Args[2]
		ui                  = os.Args[3]
	)

	cwd := "."
	baseProjectDir := cwd
	baseProjectDirUI, _ := filepath.Rel(cwd, "frontend")

	backExists, err := SubdirectoryExists(backendTemplatePath + backend)
	uiExists, err := SubdirectoryExists(uiTemplatePath + ui)
	if err != nil {
		return util.LogAndReturn(
			log.Error,
			fmt.Sprint("unexpected cli error"),
			errors.New("internal error"),
		)
	}

	if !backExists {
		err := util.LogAndReturn(
			log.Error,
			fmt.Sprint("backend template ", backend, " does not exist. check the valid templates."),
			errors.New("invalid backend template error"),
		)
		printAvailableTemplates()
		return err
	}
	if !uiExists {
		err := util.LogAndReturn(
			log.Error,
			fmt.Sprint("frontend template ", ui, " does not exist. check the valid templates."),
			errors.New("invalid frontend template error"),
		)
		printAvailableTemplates()
		return err
	}
	err = os.Mkdir(baseProjectDirUI, 0666)
	if err != nil {
		panic(err)
	}

	log.Info("Creating backend template files for " + backend)
	err = CopyFiles(backendTemplatePath+backend, baseProjectDir)
	if err != nil {
		return fmt.Errorf("creating backend files error: %v", err.Error())
	}
	log.Info("Creating frontend template files for " + ui)
	err = CopyFiles(uiTemplatePath+ui, baseProjectDirUI)
	if err != nil {
		return fmt.Errorf("creating frontend files error: %v", err.Error())
	}
	log.Info("Doing some internal work...")
	err = ResetPrefixedFiles(".")
	if err != nil {
		return fmt.Errorf("failed to rename file: %v", err)
	}
	log.Info("Done! your project is created.")
	return nil
}

func CopyFiles(src, dest string) error {
	entries, err := fs.ReadDir(content, src)
	if err != nil {
		return fmt.Errorf("error reading directory: %v", err)
	}

	for _, entry := range entries {
		err := func() error {
			srcPath := fmt.Sprint(src, "/", entry.Name())
			destPath := fmt.Sprint(dest, "/", entry.Name())

			file, err := content.Open(srcPath)
			if err != nil {
				return fmt.Errorf("error opening file %s: %v", srcPath, err)
			}
			defer file.Close()

			stat, err := file.Stat()
			if stat.IsDir() {
				/// TODO: apply to directoryes.
				err = os.Mkdir(destPath, 0666)
				if err != nil {
					return err
				}
				err = CopyFiles(srcPath, destPath)
				if err != nil {
					return err
				}
				return nil
			}

			destFile, err := os.Create(destPath)
			if err != nil {
				return fmt.Errorf("error creating file %s: %v", destPath, err)
			}
			defer destFile.Close()

			_, err = io.Copy(destFile, file)
			if err != nil {
				return fmt.Errorf("error copying file content: %v", err)
			}

			return nil
		}()

		if err != nil {
			return err
		}
	}

	return nil
}

func SubdirectoryExists(subDir string) (bool, error) {
	entries, err := fs.ReadDir(content, subDir)
	if err != nil {
		if os.IsNotExist(err) {
			return false, nil // Subdirectory does not exist
		}
		return false, fmt.Errorf("error reading directory: %v", err)
	}

	// Check if there is at least one entry, indicating the subdirectory exists
	return len(entries) > 0, nil
}

func ResetPrefixedFiles(tempPath string) error {
	return filepath.Walk(tempPath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}

		// Check if the file or directory name starts with a dot "."
		if strings.HasSuffix(info.Name(), "__") {
			// Generate the new name by removing the dot and adding a double underscore
			newName := "." + strings.Replace(info.Name(), "__", "", 1)

			// Construct the new path
			newPath := filepath.Join(filepath.Dir(path), newName)

			// Rename the file or directory
			err := os.Rename(path, newPath)
			if err != nil {
				return err
			}
		}

		return nil
	})
}
