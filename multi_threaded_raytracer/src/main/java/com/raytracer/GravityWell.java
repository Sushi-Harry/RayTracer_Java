package com.raytracer;

public class GravityWell implements Material {
    public Vector3 center;
    public double mass, eventHorizonRadius, influenceRadiusSq, stepSize;

    // Accretion Disk variables
    public double diskInnerRadius, diskOuterRadius, diskInnerSq, diskOuterSq;

    private static final ThreadLocal<Vector3> tlPos = ThreadLocal.withInitial(Vector3::new);
    private static final ThreadLocal<Vector3> tlDir = ThreadLocal.withInitial(Vector3::new);
    private static final ThreadLocal<Vector3> tlToCenter = ThreadLocal.withInitial(Vector3::new);
    private static final ThreadLocal<Vector3> tlStep = ThreadLocal.withInitial(Vector3::new);

    public GravityWell(Vector3 center, double mass, double eventHorizonRadius, double influenceRadius, double stepSize){
        this.center = center;
        this.mass = mass;
        this.eventHorizonRadius = eventHorizonRadius;
        this.influenceRadiusSq = influenceRadius * influenceRadius;
        this.stepSize = stepSize;

        this.diskInnerRadius = eventHorizonRadius + 0.05;
        this.diskOuterRadius = 1.4;
        this.diskInnerSq = diskInnerRadius * diskInnerRadius;
        this.diskOuterSq = diskOuterRadius * diskOuterRadius;
    }

    @Override
    public boolean Scatter(Ray r_in, HitRecord rec, Vector3 attenuation) {
        attenuation.x = 1.0;
        attenuation.y = 1.0;
        attenuation.z = 1.0;

        Vector3 pos = tlPos.get().Copy(rec.p);
        Vector3 dir = tlDir.get().Copy(r_in.direction).Normalize();
        Vector3 toCenter = tlToCenter.get();
        Vector3 stepVec = tlStep.get();

        // Starting a lil bit inside the influence sphere so that the loop doesn't immediately exit
        stepVec.Copy(dir).MultiplyInPlace(stepSize);
        pos.AddInPlace(stepVec);

        int maxSteps = 10000;
        for(int i = 0; i < maxSteps; i++){
            // Vector pointing to the Center of the sphere MINUS the current toCenter vector
            toCenter.Copy(center).SubtractInPlace(pos);
            double distSq = toCenter.LengthSquared();

            // This is the condition to check if the ray is inside the event horizon (Length of toCenter vector is less than radius of event horizon)
            // If it's true then just return a black color / returning false through this function
            if(distSq < eventHorizonRadius * eventHorizonRadius){
                return false;
            }

            // Accretion disk emissive gglow
            double relativeY = pos.y - center.y;
            double relativeX = pos.x - center.x;
            double relativeZ = pos.z - center.z;
            double distXZSq = (relativeX * relativeX) + (relativeZ * relativeZ);

            if(Math.abs(relativeY) < 0.02 && distXZSq > diskInnerSq && distXZSq < diskOuterSq){
                double distXZ = Math.sqrt(distXZSq);

                double t = (distXZ - diskInnerRadius) / (diskOuterRadius - diskInnerRadius);
                // This is the thermal gradient calculation (It's white / blue at center cause its HOTTT but fades to orange / red at the edge) 
                attenuation.x = 12.0 * (1.0 - t) + 2.0 * t;
                attenuation.y = 10.0 * (1.0 - t) + 0.5 * t;
                attenuation.z = 14.0 * (1.0 - t) + 0.0 * t;

                r_in.origin.Copy(pos);
                r_in.direction.x = 0.0;
                r_in.direction.y = 1.0;
                r_in.direction.z = 0.0;
                return true;
            }

            // If it got out of the sphere somehow, then this condition triggers
            if(distSq > influenceRadiusSq){
                r_in.origin.Copy(pos);
                r_in.direction.Copy(dir);
                return true;
            }

            // Applying the math part to simulate the ray direction
            // basically the inverse square law (F = M / (r*r))
            double force = (mass / distSq) * stepSize ;
            toCenter.Normalize().MultiplyInPlace(force);
            dir.AddInPlace(toCenter).Normalize();

            // Stepping the ray forward using the new direction
            stepVec.Copy(dir).MultiplyInPlace(stepSize);
            pos.AddInPlace(stepVec);
        }

        // This condition triggers when the ray gets trapped in the sphere which means it keeps orbiting the sphere forever
        return false;
    }
        
}
