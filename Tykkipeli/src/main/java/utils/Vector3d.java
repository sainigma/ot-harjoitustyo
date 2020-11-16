/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author suominka
 */
public class Vector3d {
    public double x,y,z; //Public for simplicitys sake
    public float x_f,y_f,z_f;
    public Vector3d(){
        x=0;
        y=0;
        z=0;
    }
    public Vector3d(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public Vector3d(double x){
        this.x=x;
        this.y=x;
        this.z=x;
    }
    @Override
    public Vector3d clone(){
        return new Vector3d(x,y,z);
    }
    public Vector3d diff(Vector3d b){
        return new Vector3d(x-b.x,y-b.y,z-b.z);
    }
    @Override
    public String toString(){
        return "x: "+x+", y: "+y+", z: "+z;
    }
}
