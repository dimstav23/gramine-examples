package main

import (
	"image"
	"image/color"
	"log"
	"time"

	"github.com/anthonynsimon/bild/blend"
	"github.com/anthonynsimon/bild/blur"
	"github.com/anthonynsimon/bild/effect"
	"github.com/anthonynsimon/bild/imgio"
	"github.com/anthonynsimon/bild/transform"
)

func applyAlpha(img image.Image, alpha float64) *image.RGBA {
	bounds := img.Bounds()
	result := image.NewRGBA(bounds)
	for y := bounds.Min.Y; y < bounds.Max.Y; y++ {
		for x := bounds.Min.X; x < bounds.Max.X; x++ {
			r, g, b, a := img.At(x, y).RGBA()
			a = uint32(float64(a) * alpha)
			result.Set(x, y, color.RGBA{uint8(r >> 8), uint8(g >> 8), uint8(b >> 8), uint8(a >> 8)})
		}
	}
	return result
}

func processImage(inputPath, watermarkPath, outputPath string) error {
	img, err := imgio.Open(inputPath)
	if err != nil {
		return err
	}

	// Step 1: Apply Gaussian Blur
	blurred := blur.Gaussian(img, 5.0)

	// Step 2: Perform Edge Detection
	edges := effect.Sobel(blurred)

	// Step 3: Resize the Image
	resized := transform.Resize(edges, 4000, 4000, transform.Linear)

	// Step 4: Add Watermark
	watermark, err := imgio.Open(watermarkPath)
	if err != nil {
		return err
	}

	resizedWatermark := transform.Resize(watermark, resized.Bounds().Dx(), resized.Bounds().Dy(), transform.Linear)
	transparentWatermark := applyAlpha(resizedWatermark, 0.3)
	watermarked := blend.Overlay(resized, transparentWatermark)

	// Save the final image
	if err := imgio.Save(outputPath, watermarked, imgio.JPEGEncoder(95)); err != nil {
		return err
	}

	return nil
}

func main() {
	startTime := time.Now()
	err := processImage("input.png", "watermark.png", "output.png")
	if err != nil {
		log.Fatalf("Failed to process image: %v", err)
	}
	executionTime := time.Since(startTime).Seconds()
	log.Printf("Image processed successfully\n")
	log.Printf("Execution time: %f seconds\n", executionTime)
}
