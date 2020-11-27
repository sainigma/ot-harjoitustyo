/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import static java.lang.Math.sqrt;

/**
 *
 * @author suominka
 */
public class Vector3d {
    public double x, y, z; //Public for simplicitys sake
    public Vector3d() {
        x = 0;
        y = 0;
        z = 0;
    }
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3d(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    public Vector3d(double x) {
        this.x = x;
        this.y = x;
        this.z = x;
    }
    public double magnitude() {
        return sqrt(x * x + y * y + z * z);
    }
    @Override
    public Vector3d clone() {
        return new Vector3d(x, y, z);
    }
    public Vector3d add(Vector3d b) {
        return new Vector3d(this.x + b.x, this.y + b.y, this.z + b.z);
    }
    public Vector3d scale(double s) {
        return new Vector3d(this.x * s, this.y * s, this.z * s);
    }
    public Vector3d diff(Vector3d b) {
        return new Vector3d(x - b.x, y - b.y, z - b.z);
    }
    public Vector3d lerpSigmoid(Vector3d a, Vector3d b, float t) {
        return null;
    }
    public Vector3d lerp(Vector3d a, Vector3d b, float t) {
        return a.scale(1 - t).add(b.scale(t));
    }
    public void rotateX(double angle) {
        double rads = deg2Rad(angle);
        double y0 = y;
        double z0 = z;
        y = y0 * Math.cos(rads) - z0 * Math.sin(rads);
        z = y0 * Math.sin(rads) + z0 * Math.cos(rads);
    }
    public void rotateY(double angle) {
        double rads = deg2Rad(angle);
        double x0 = x;
        double z0 = z;
        x = x0 * Math.cos(rads) + z0 * Math.sin(rads);
        z = -x0 * Math.sin(rads) + z0 * Math.cos(rads);
    }
    public void rotateZ(double angle) {
        double rads = deg2Rad(angle);
        double x0 = x;
        double y0 = y;
        x = x0 * Math.cos(rads) - y0 * Math.sin(rads);
        y = x0 * Math.sin(rads) + y0 * Math.cos(rads);
    }
    private double deg2Rad(double degs) {
        return (degs / 360f) * Math.PI * 2f;
    }
    public void normalize(){
        double factor = 1f/magnitude();
        set(this.scale(factor));
    }
    public void set(Vector3d toCopy) {
        x = toCopy.x;
        y = toCopy.y;
        z = toCopy.z;
    }
    public void setByAzimuthAltitude(double azimuth, double altitude) {
        x = 1;
        y = 0;
        z = 0;
        rotateZ(altitude);
        rotateY(azimuth);
    }
    @Override
    public String toString() {
        return "x: " + x + ", y: " + y + ", z: " + z;
    }
}
