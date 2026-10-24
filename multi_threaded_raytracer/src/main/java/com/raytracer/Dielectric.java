package com.raytracer;

import java.util.concurrent.ThreadLocalRandom;

public class Dielectric implements Material {

    // Refractive index
    public double ir;

    private static final ThreadLocal<Vector3> threadLocalTempDir = ThreadLocal.withInitial(Vector3::new);
    private static final ThreadLocal<Vector3> threadLocalTempScattered = ThreadLocal.withInitial(Vector3::new);

    public Dielectric(double refractive_idx){
        this.ir = refractive_idx;
    }

    private static double reflectance(double cosine, double ref_idx){
        double r0 = (1 - ref_idx) / (1 + ref_idx);
        r0 = r0 * r0;
        return r0 + (1 - r0) * Math.pow((1 - cosine), 5);
    }

    @Override
    public boolean Scatter(Ray r_in, HitRecord rec, Vector3 attenuation) {
        attenuation.x = 1.0;
        attenuation.y = 1.0;
        attenuation.z = 1.0;

        // This one basically helps calculate stuff for 2 cases:
        // 1. Rays going out of the object
        // 2. Rays going into the object
        double refraction_ratio = rec.frontFace ? (1.0 / ir) : ir;

        Vector3 unitDir = threadLocalTempDir.get().Copy(r_in.direction).Normalize();
        Vector3 scatteredDir = threadLocalTempScattered.get();

        double cos_theta = Math.min((-unitDir.x * rec.normal.x) - (unitDir.y * rec.normal.y) - (unitDir.z * rec.normal.z), 1.0);
        double sin_theta = Math.sqrt(1.0 - cos_theta * cos_theta);

        // Total Internal Reflection condition
        boolean cannot_refract = refraction_ratio * sin_theta > 1.0;

        if(cannot_refract || reflectance(cos_theta, refraction_ratio) > ThreadLocalRandom.current().nextDouble()){
            Vector3.Reflect(unitDir, rec.normal, scatteredDir);
        }else{
            Vector3.Refract(unitDir, rec.normal, refraction_ratio, scatteredDir);
        }

        r_in.origin.Copy(rec.p);
        r_in.direction.Copy(scatteredDir);
        return true;
    }
    
}
