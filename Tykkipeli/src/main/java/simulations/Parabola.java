/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulations;

/**
 *
 * @author suominka
 */
import utils.Vector3d;
public class Parabola extends PhysicsSolver{
    public Parabola(){
        super();
        set(new Vector3d(),new Vector3d(100,100,0),0.0001);
    }
}
