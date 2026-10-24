package com.raytracer;

public class Ray {
    public final Vector3 origin;
    public final Vector3 direction;

    public Ray(Vector3 origin, Vector3 direction){
        this.origin = origin;
        this.direction = direction;
    }

    public Vector3 At(double t, Vector3 out){
        // The formula being used here is: out = direction * t + origin
        out.Copy(direction).MultiplyInPlace(t).AddInPlace(origin);
        return out;
    }
}
