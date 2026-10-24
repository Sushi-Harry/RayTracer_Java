package com.raytracer;

public class Metal implements Material{

    public Vector3 albedo;
    public double fuzz;
    private static final ThreadLocal<Vector3> threadLocalTempReflected = ThreadLocal.withInitial(Vector3::new);
    private static final ThreadLocal<Vector3> threadLocalTempFuzz = ThreadLocal.withInitial(Vector3::new);

    public Metal(Vector3 albedo, double fuzz){
        this.albedo = albedo;
        this.fuzz = fuzz < 1 ? fuzz : 1;
    }

    @Override
    public boolean Scatter(Ray r_in, HitRecord rec, Vector3 attenuation) {
        if(!rec.frontFace) return false;

        Vector3 tempReflected = threadLocalTempReflected.get();
        Vector3 tempFuzz = threadLocalTempFuzz.get();

        Vector3.Reflect(new Vector3().Copy(r_in.direction).Normalize(), rec.normal, tempReflected);

        Utilities.RandomUnitSphere(tempFuzz);
        tempFuzz.MultiplyInPlace(fuzz);
        tempReflected.AddInPlace(tempFuzz);

        if(tempReflected.DotProduct(rec.normal) > 0){
            r_in.origin.Copy(rec.p);
            r_in.direction.Copy(tempReflected);
            attenuation.Copy(albedo);

            return true;
        }

        return false;
    }
    
}
