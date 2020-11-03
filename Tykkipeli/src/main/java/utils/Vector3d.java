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