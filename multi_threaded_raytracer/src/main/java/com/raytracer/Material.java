package com.raytracer;

public interface Material {
    boolean Scatter(Ray r_in, HitRecord rec, Vector3 attenuation);
}
