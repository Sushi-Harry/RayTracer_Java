package com.raytracer;

public interface Hittable {
    boolean Hit(Ray r, double tMin, double tMax, HitRecord rec);
}
