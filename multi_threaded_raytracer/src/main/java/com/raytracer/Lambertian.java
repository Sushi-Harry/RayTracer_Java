package com.raytracer;

public class Lambertian implements Material{
    public Vector3 albedo;
    private static final ThreadLocal<Vector3> threadLocalTempScatter = ThreadLocal.withInitial(Vector3::new);

    public Lambertian(Vector3 albedo){
        this.albedo = albedo;
    }

    @Override
    public boolean Scatter(Ray r_in, HitRecord rec, Vector3 attenuation){
        if(!rec.frontFace) return false;

        Vector3 tempScatter = threadLocalTempScatter.get();

        Utilities.RandomUnitVector(tempScatter);
        tempScatter.AddInPlace(rec.normal);

        if(tempScatter.LengthSquared() < 1e-8){
            tempScatter.Copy(rec.normal);
        }

        r_in.origin.Copy(rec.p);
        r_in.direction.Copy(tempScatter);
        attenuation.Copy(albedo);
        return true;
    }
}
