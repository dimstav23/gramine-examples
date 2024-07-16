import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessing {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ImageProcessing <image_path>");
            return;
        }

        String imagePath = args[0];

        try {
            BufferedImage image = ImageIO.read(new File(imagePath));

            long startTime = System.currentTimeMillis();

            // Resize the image
            BufferedImage resizedImage = resizeImage(image, 512, 512);

            // Apply multiple layers of processing
            BufferedImage processedImage = resizedImage;
            for (int i = 0; i < 3; i++) {
                processedImage = convertToGrayscale(processedImage);
                processedImage = applyGaussianBlur(processedImage, 5);
                processedImage = applyGaussianBlur(processedImage, 3);
                processedImage = applySharpen(processedImage);
                processedImage = detectEdges(processedImage);
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Processing complete.");
            System.out.println("Time taken: " + (endTime - startTime) + " milliseconds");

            // Save the resulting image
            File outputfile = new File("processed_image.jpg");
            ImageIO.write(processedImage, "jpg", outputfile);

        } catch (IOException e) {
            System.out.println("Error loading or processing image: " + e.getMessage());
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }

    private static BufferedImage convertToGrayscale(BufferedImage originalImage) {
        BufferedImage grayImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        return grayImage;
    }

    private static BufferedImage applyGaussianBlur(BufferedImage image, int radius) {
        int size = 2 * radius + 1;
        float[] kernel = new float[size * size];
        float sigma = 1.0f;
        float sum = 0.0f;
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                float weight = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[(y + radius) * size + (x + radius)] = weight;
                sum += weight;
            }
        }
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        BufferedImage blurredImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int y = radius; y < image.getHeight() - radius; y++) {
            for (int x = radius; x < image.getWidth() - radius; x++) {
                float sumR = 0;
                float sumG = 0;
                float sumB = 0;
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int rgb = image.getRGB(x + kx, y + ky);
                        sumR += ((rgb >> 16) & 0xff) * kernel[(ky + radius) * size + (kx + radius)];
                        sumG += ((rgb >> 8) & 0xff) * kernel[(ky + radius) * size + (kx + radius)];
                        sumB += (rgb & 0xff) * kernel[(ky + radius) * size + (kx + radius)];
                    }
                }
                int r = Math.min(Math.max((int) sumR, 0), 255);
                int g = Math.min(Math.max((int) sumG, 0), 255);
                int b = Math.min(Math.max((int) sumB, 0), 255);
                blurredImage.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return blurredImage;
    }

    private static BufferedImage applySharpen(BufferedImage image) {
        float[] sharpenKernel = {
            0, -1, 0,
            -1, 5, -1,
            0, -1, 0
        };
        BufferedImage sharpenedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                float sumR = 0;
                float sumG = 0;
                float sumB = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = image.getRGB(x + kx, y + ky);
                        float weight = sharpenKernel[(ky + 1) * 3 + (kx + 1)];
                        sumR += ((rgb >> 16) & 0xff) * weight;
                        sumG += ((rgb >> 8) & 0xff) * weight;
                        sumB += (rgb & 0xff) * weight;
                    }
                }
                int r = Math.min(Math.max((int) sumR, 0), 255);
                int g = Math.min(Math.max((int) sumG, 0), 255);
                int b = Math.min(Math.max((int) sumB, 0), 255);
                sharpenedImage.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        return sharpenedImage;
    }

    private static BufferedImage detectEdges(BufferedImage image) {
        float[] sobelX = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        float[] sobelY = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
        
        BufferedImage edgeImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                float sumX = 0;
                float sumY = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int rgb = image.getRGB(x + kx, y + ky) & 0xff;
                        sumX += rgb * sobelX[(ky + 1) * 3 + (kx + 1)];
                        sumY += rgb * sobelY[(ky + 1) * 3 + (kx + 1)];
                    }
                }
                int magnitude = Math.min(Math.max((int) Math.sqrt(sumX * sumX + sumY * sumY), 0), 255);
                edgeImage.setRGB(x, y, (magnitude << 16) | (magnitude << 8) | magnitude);
            }
        }
        return edgeImage;
    }
}
