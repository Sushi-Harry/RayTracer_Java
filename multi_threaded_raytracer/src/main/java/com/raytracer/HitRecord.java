package com.raytracer;

public class HitRecord {
    public final Vector3 p = new Vector3();
    public final Vector3 normal = new Vector3();
    public double t;
    public boolean frontFace;
    public Material mat;

    public void Copy(HitRecord oHitRecord){
        this.p.Copy(oHitRecord.p);
        this.normal.Copy(oHitRecord.normal);
        this.t = oHitRecord.t;
        this.frontFace = oHitRecord.frontFace;
        this.mat = oHitRecord.mat;
    }
}
