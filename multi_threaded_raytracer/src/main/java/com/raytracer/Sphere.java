package com.raytracer;

public class Sphere implements Hittable {
    public Vector3 center;
    public double radius;
    public Material mat;

    public Sphere(Vector3 center, double radius, Material mat){
        this.center = center;
        this.radius = radius;
        this.mat = mat;
    }

    @Override 
    public boolean Hit(Ray r, double tMin, double tMax, HitRecord rec){
        double oc_x = r.origin.x - center.x;
        double oc_y = r.origin.y - center.y;
        double oc_z = r.origin.z - center.z;

        double a = r.direction.LengthSquared();
        double half_b = (oc_x * r.direction.x) + (oc_y * r.direction.y) + (oc_z * r.direction.z);
        double c = (oc_x * oc_x + oc_y * oc_y + oc_z * oc_z) - radius * radius;

        double discriminant = half_b * half_b - a * c;
        if (discriminant < 0) return false;

        double sqrtd = Math.sqrt(discriminant);

        // Finding the nearest root for the intersection that is acceptable 
        double root = (-half_b - sqrtd) / a;
        if(root <= tMin || root >= tMax){
            root = (-half_b + sqrtd) / a;
            if (root <= tMin || root >= tMax) {
                return false;
            }
        }

        rec.t = root;
        r.At(rec.t, rec.p);

        // Outward normal which is calculated as (P-C) / radius
        rec.normal.Copy(rec.p).SubtractInPlace(center).MultiplyInPlace(1.0 / radius);
        // Determining if the ray has intersected the front face or if it's inside the object
        rec.frontFace = r.direction.DotProduct(rec.normal) < 0;
        if(!rec.frontFace){
            rec.normal.MultiplyInPlace(-1.0);
        }
        rec.mat = this.mat;
        return true;
    }
}
