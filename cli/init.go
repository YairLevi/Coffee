package main

import (
	"fmt"
	"io"
	"io/fs"
	"os"
	"path/filepath"
)

func Init() error {
	var (
		uiTemplatePath      = "templates/ui/"
		backendTemplatePath = "templates/backend/"
		backend             = os.Args[2]
		ui                  = os.Args[3]
	)

	cwd := "."
	baseProjectDir := cwd
	baseProjectDirUI, _ := filepath.Rel(cwd, "frontend")

	err := os.Mkdir(baseProjectDirUI, 0666)
	if err != nil {
		panic(err)
	}

	backExists, err := SubdirectoryExists(backendTemplatePath + backend)
	uiExists, err := SubdirectoryExists(uiTemplatePath + ui)
	if err != nil {
		return fmt.Errorf("init util unexpected error: %v", err)
	}

	if !backExists {
		return fmt.Errorf("invalid backend template")
	}
	if !uiExists {
		return fmt.Errorf("invalid ui template")
	}

	err = CopyFiles(backendTemplatePath+backend, baseProjectDir)
	if err != nil {
		return fmt.Errorf("creating backend files error: %v", err.Error())
	}
	err = CopyFiles(uiTemplatePath+ui, baseProjectDirUI)
	if err != nil {
		return fmt.Errorf("creating frontend files error: %v", err.Error())
	}
	// err = os.Rename("gitignore", ".gitignore")
	// err = os.Rename("frontend/gitignore", "frontend/.gitignore")
	if err != nil {
		return fmt.Errorf("failed to rename file: %v", err)
	}

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
