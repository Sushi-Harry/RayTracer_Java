package com.raytracer;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void RayColor(Ray r, Vector3 outColor){
        Vector3 unitDirection = new Vector3().Copy(r.direction).Normalize();

        double a = 0.5 * (unitDirection.y + 1.0);
        outColor.x = (1.0 - a) * 1.0 + a * 0.5;
        outColor.y = (1.0 - a) * 1.0 + a * 0.7;
        outColor.z = (1.0 - a) * 1.0 + a * 1.5;
    }
    public static void RayColor(Ray r, Vector3 outColor, Vector3 tempNormal){
        Vector3 sphereCenter = new Vector3(0, 0, -1);
        double t = HitSphere(sphereCenter, 0.5, r);

        if(t > 0.0){
            r.At(t, tempNormal);
            tempNormal.SubtractInPlace(sphereCenter).Normalize();

            outColor.x = 0.5 * (tempNormal.x + 1.0);
            outColor.y = 0.5 * (tempNormal.y + 1.0);
            outColor.z = 0.5 * (tempNormal.z + 1.0);
            return;
        }

        Vector3 unitDirection = new Vector3().Copy(r.direction).Normalize();

        double a = 0.5 * (unitDirection.y + 1.0);
        outColor.x = (1.0 - a) * 1.0 + a * 0.5;
        outColor.y = (1.0 - a) * 1.0 + a * 0.7;
        outColor.z = (1.0 - a) * 1.0 + a * 1.5;
    }
    public static void RayColor(Ray r, Hittable world, HitRecord rec, Vector3 outColor){
        if(world.Hit(r, 0.001, Double.POSITIVE_INFINITY, rec)){
            outColor.x = 0.5 * (rec.normal.x + 1.0);
            outColor.y = 0.5 * (rec.normal.y + 1.0);
            outColor.z = 0.5 * (rec.normal.z + 1.0);
            return;
        }

        // Background Gradient Color output ahahahaha
        Vector3 unitDirection = new Vector3().Copy(r.direction).Normalize();
        double a = 0.5 * (unitDirection.y + 1.0);
        outColor.x = (1.0 - a) * 1.0 + a * 0.5;
        outColor.y = (1.0 - a) * 1.0 + a * 0.7;
        outColor.z = (1.0 - a) * 1.0 + a * 1.0;
    }
    public static void RayColor_Lit(Ray r, Hittable world, HitRecord rec, Vector3 outColor){
        int maxdepth = 50;
        double att_x = 1.0;
        double att_y = 1.0;
        double att_z = 1.0;

        Vector3 bounceAttenuation = new Vector3();
        Vector3 unitDirection = new Vector3();

        for(int depth = 0; depth < maxdepth; depth++){
            if(world.Hit(r, 0.001, Double.POSITIVE_INFINITY, rec)){
                if(rec.mat != null && rec.mat.Scatter(r, rec, bounceAttenuation)){
                    att_x *= bounceAttenuation.x;
                    att_y *= bounceAttenuation.y;
                    att_z *= bounceAttenuation.z;
                }else{
                    outColor.x = 0;
                    outColor.y = 0;
                    outColor.z = 0;
                    return;
                }
            }else{
                unitDirection.Copy(r.direction).Normalize();
                double a = 0.5 * (unitDirection.y + 1.0);

                double bg_x = (1.0 - a) * 1.0 + a * 0.5;
                double bg_y = (1.0 - a) * 1.0 + a * 0.5;
                double bg_z = (1.0 - a) * 1.0 + a * 0.5;

                outColor.x = bg_x *att_x;
                outColor.y = bg_y *att_y;
                outColor.z = bg_z *att_z;
                return;
            }
        }
        // The function comes to this point when all the light is absorbed (Or rather, when max depth has been reached / exceeded)
        outColor.x = 0.0;
        outColor.y = 0.0;
        outColor.z = 0.0;
    }

    public static double HitSphere(Vector3 center, double radius, Ray r){
        double oc_x = r.origin.x - center.x;
        double oc_y = r.origin.y - center.y;
        double oc_z = r.origin.z - center.z;

        double a = r.direction.LengthSquared();

        double half_b = (oc_x * r.direction.x) + (oc_y * r.direction.y) + (oc_z * r.direction.z);

        double c = (oc_x * oc_x + oc_y * oc_y + oc_z * oc_z) - radius*radius;

        double discriminant = half_b * half_b - a*c;

        if(discriminant < 0.0){
            return -1.0;
        }else{
            return (-half_b - Math.sqrt(discriminant)) / a;
        }
    }

    public static void main(String[] args) {
        double aspectRatio = 4.0 / 3.0;
        int imageWidth = 800;
        int imageHeight = (int)(imageWidth / aspectRatio);

        // Camera
        Vector3 cameraOrigin = new Vector3();
        double viewportHeight = 2.0;
        double viewportWidth = aspectRatio * viewportHeight;
        double focalLength = 1.0;
        double moveSpeed = 0.01;

        Vector3 horizontal = new Vector3(viewportWidth, 0, 0);
        Vector3 vertical = new Vector3(0, viewportHeight, 0);

        Vector3 lowerLeftCorner = new Vector3(
            cameraOrigin.x - horizontal.x/2 - vertical.x/2,
            cameraOrigin.y - horizontal.y/2 - vertical.y/2,
            cameraOrigin.z - horizontal.z/2 - vertical.z/2 - focalLength
        );

        // Javva's built in framebuffer
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        RenderWindow window = new RenderWindow(imageWidth, imageHeight, image);
        
        // Preallocating the objects
        HittableList world = new HittableList();
        Material blackHoleMat = new GravityWell(new Vector3(0, 0, -4.0), 0.6, 0.4, 2.0, 0.02);
        Material normalMat = new Lambertian(new Vector3(0.8, 0.3, 0.3));

        Sphere interactiveSphere = new Sphere(new Vector3(0, 0, -7.0), 0.5, normalMat);
        world.Add(interactiveSphere);
        world.Add(new Sphere(new Vector3(0, 0, -4.0), 2.0, blackHoleMat));
        // Now the floor object
        Material floorMat = new Lambertian(new Vector3(0.5, 0.5, 0.5));
        world.Add(new Sphere(new Vector3(0, -100.5, -1.0), 100, floorMat));

        // Multithreading stuff
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(cores);
        int gridDivisions = 16;
        int tileWidth = (int)Math.ceil((double) imageWidth / gridDivisions);
        int tileHeight = (int) Math.ceil((double) imageHeight / gridDivisions);

        // Main rendering loop
        System.out.println("[Started Rendering]");
        System.out.println("[Cores in Use: " + cores + "]");
        while(true){
            boolean cameraMoved = false;
            if(window.movingForward) { cameraOrigin.z -= moveSpeed; cameraMoved = true;}
            if(window.movingBackward) { cameraOrigin.z += moveSpeed; cameraMoved = true; }
            if(window.movingLeft) { cameraOrigin.x -= moveSpeed; cameraMoved = true; }
            if(window.movingRight) { cameraOrigin.x += moveSpeed; cameraMoved = true; }

            if(cameraMoved){
                lowerLeftCorner.x = cameraOrigin.x - horizontal.x / 2 - vertical.x / 2;
                lowerLeftCorner.y = cameraOrigin.y - horizontal.y / 2 - vertical.y / 2;
                lowerLeftCorner.z = cameraOrigin.z - horizontal.z / 2 - vertical.z / 2 - focalLength;
            }

            // This can be adapted to work for all the spheres in the scene
            interactiveSphere.center.x = window.targetX * 3.0;
            interactiveSphere.center.y = window.targetY * 3.0;
            interactiveSphere.center.z = window.targetZ - 6.0;

            int currentSamples = (window.isMoving || window.isGuiMoving) ? 1 : 50;
            for (int y = 0; y < imageHeight; y += tileHeight) {
                for (int x = 0; x < imageWidth; x += tileWidth) {
                    
                    // Ensure edge tiles don't bleed out of bounds
                    int currentTileWidth = Math.min(tileWidth, imageWidth - x);
                    int currentTileHeight = Math.min(tileHeight, imageHeight - y);
    
                    RenderTask task = new RenderTask(x, y, currentTileWidth, currentTileHeight, imageWidth, imageHeight, currentSamples, world, image, cameraOrigin, horizontal, vertical, lowerLeftCorner);
                    pool.submit(task);
                }
            }
            pool.shutdown();
            try {
                pool.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            window.refresh();
            pool = Executors.newFixedThreadPool(cores);
        }
    }
}