package com.raytracer;

import java.util.ArrayList;

public class HittableList implements Hittable{
    public ArrayList<Hittable> objects = new ArrayList<>();

    private static final ThreadLocal<HitRecord> threadLocalTempRec = ThreadLocal.withInitial(HitRecord::new );

    public HittableList() {}
    public HittableList(Hittable object) { Add(object); }

    public void Add(Hittable object) { objects.add(object); }
    public void Clear() { objects.clear(); }
    @Override
    public boolean Hit(Ray r, double tMin, double tMax, HitRecord rec) {
        HitRecord tempRec = threadLocalTempRec.get();
        boolean hitAnything = false;
        double closestSoFar = tMax;

        for(Hittable obj : objects){
            if(obj.Hit(r, tMin, closestSoFar, tempRec)){
                hitAnything = true;
                closestSoFar = tempRec.t;
                rec.Copy(tempRec);
            }
        }
        return hitAnything;
    }
}
