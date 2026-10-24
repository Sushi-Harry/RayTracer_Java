package com.raytracer;

import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class RenderTask implements Runnable {
    int startX, startY, width, height, imageWidth, imageHeight, samples;
    Hittable world;
    BufferedImage image;
    Vector3 cameraOrigin, horizontal, vertical, lowerLeftCorner;

    // ThreadLocal temporary variables
    Vector3 rayOrigin = new Vector3();
    Vector3 rayDirection = new Vector3();
    Vector3 pixelColorAccumulator = new Vector3();
    Vector3 tempColor = new Vector3();
    HitRecord rec = new HitRecord();
    Ray ray = new Ray(rayOrigin, rayDirection);

    public RenderTask(int startX, int startY, int width, int height, int imageWidth, int imageHeight, int samples, Hittable world, BufferedImage image, Vector3 cameraOrigin, Vector3 horizontal, Vector3 vertical, Vector3 lowerLeftCorner){
        this.startX = startX; this.startY = startY;
        this.width = width; this.height = height;
        this.imageWidth = imageWidth; this.imageHeight = imageHeight;
        this.samples = samples; this.world = world; this.image = image;
        this.cameraOrigin = cameraOrigin; this.horizontal = horizontal;
        this.vertical = vertical; this.lowerLeftCorner = lowerLeftCorner;
    }

    @Override
    public void run() {
        // Now this function is where it gets interesting
        // For each thread, just rendering a defined part of the screen makes it much faster than rendering everything row by row, pixel by pixel
        for(int j = startY; j < startY + height && j < imageHeight; j++){
            for(int i = startX; i < startX + width && i < imageWidth; i++){
                pixelColorAccumulator.x = 0;
                pixelColorAccumulator.y = 0;
                pixelColorAccumulator.z = 0;

                for(int s = 0; s < samples; s++){
                    double u = (double)(i + ThreadLocalRandom.current().nextDouble()) / (imageWidth-1);
                    double v = (double)((imageHeight-1-j) + ThreadLocalRandom.current().nextDouble()) / (imageHeight-1);

                    rayOrigin.Copy(cameraOrigin);
                    rayDirection.Copy(lowerLeftCorner);
                    rayDirection.x += u * horizontal.x + v * vertical.x - cameraOrigin.x;
                    rayDirection.y += u * horizontal.y + v * vertical.y - cameraOrigin.y;
                    rayDirection.z += u * horizontal.z + v * vertical.z - cameraOrigin.z;

                    Main.RayColor_Lit(ray, world, rec, tempColor);
                    pixelColorAccumulator.AddInPlace(tempColor);
                }
                double scale = 1.0 / samples;
                double r_val = Math.sqrt(pixelColorAccumulator.x * scale);
                double g_val = Math.sqrt(pixelColorAccumulator.y * scale);
                double b_val = Math.sqrt(pixelColorAccumulator.z * scale);

                int r = (int)(255 * Utilities.Clamp(r_val, 0.0, 0.999));
                int g = (int)(255 * Utilities.Clamp(g_val, 0.0, 0.999));
                int b = (int)(255 * Utilities.Clamp(b_val, 0.0, 0.999));

                int rgb = (r << 16) | (g << 8) | b;
                
                // BufferedImage.setRGB is inherently thread-safe in Java
                image.setRGB(i, j, rgb);
            }
        }
    }
    
}
