package com.raytracer;

import java.util.concurrent.ThreadLocalRandom;

public class Utilities {
    public static Vector3 Random(double min, double max, Vector3 out){
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        out.x = min + (max - min) * rand.nextDouble();
        out.y = min + (max - min) * rand.nextDouble();
        out.z = min + (max - min) * rand.nextDouble();
        return out;
    }

    public static Vector3 RandomUnitSphere(Vector3 out){
        while (true) {
            Random(-1.0, 1.0, out);
            if(out.LengthSquared() < 1.0){
                return out;
            }
        }
    }

    public static Vector3 RandomUnitVector(Vector3 out){
        return RandomUnitSphere(out).Normalize();
    }

    public static double Clamp(double x, double min, double max){
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
}
