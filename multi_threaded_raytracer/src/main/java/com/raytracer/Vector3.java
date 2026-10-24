package com.raytracer;

public class Vector3 {
    public double x, y, z;

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 AddInPlace(Vector3 other){
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3 Copy(Vector3 other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 SubtractInPlace(Vector3 other){
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3 MultiplyInPlace(double k){
        this.x *= k;
        this.y *= k;
        this.z *= k;
        return this;
    }

    public double LengthSquared(){
        return x*x + y*y + z*z;
    }

    public double Length(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3 Normalize(){
        return MultiplyInPlace(1.0 / Length());
    }

    public double DotProduct(Vector3 other){
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }

    public Vector3 CrossProduct(Vector3 _in, Vector3 _out){
        _out.x = this.y * _in.z - this.z * _in.y;
        _out.y = this.z * _in.x - this.x * _in.z;
        _out.z = this.x * _in.y - this.y * _in.x;
        return _out;
    }

    public static Vector3 Reflect(Vector3 v, Vector3 n, Vector3 out){
        double dot = v.DotProduct(n);
        out.Copy(n).MultiplyInPlace(2.0*dot);

        out.x = v.x - out.x;
        out.y = v.y - out.y;
        out.z = v.z - out.z;

        return out;
    }

    public static Vector3 Refract(Vector3 uv, Vector3 n, double etai_over_etat, Vector3 out){
        double cos_theta = Math.min((-uv.x * n.x) + (-uv.y * n.y) + (-uv.z * n.z), 1.0);
        // Perpendicular Ray Component
        double r_out_perp_x = etai_over_etat * (uv.x + cos_theta * n.x);
        double r_out_perp_y = etai_over_etat * (uv.y + cos_theta * n.y);
        double r_out_perp_z = etai_over_etat * (uv.z + cos_theta * n.z);
        // Parallel Ray Component
        double length_squared = r_out_perp_x*r_out_perp_x + r_out_perp_y*r_out_perp_y + r_out_perp_z*r_out_perp_z;
        double multiplier = -Math.sqrt(Math.abs(1.0 - length_squared));
        
        out.x = r_out_perp_x + n.x * multiplier;
        out.y = r_out_perp_y + n.y * multiplier;
        out.z = r_out_perp_z + n.z * multiplier;
        return out;
    }
}
