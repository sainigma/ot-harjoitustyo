/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic.controllers;

/**
 *
 * @author suominka
 */

public class Projectile {
    float weight = -1;
    float frontalArea;
    float dragCoeff = 1;
    int cartouches;
    boolean initOk;
    public Projectile(float weight, int cartouches) {
        if (weight <= 0 || cartouches <= 0) {
            return;
        }
        this.weight = weight;
        frontalArea = (float)(0.229f*0.229f*Math.PI/4f);
        this.cartouches = cartouches;
    }
    public void addCartouches(int amount) {
        cartouches += amount;
    }
    public int getCartouches() {
        return cartouches;
    }
    public double getInitialVelocity() {
        return 335*(weight/123f)*cartouches/3f;
    }
    public boolean initOk() {
        return weight > 0;
    }
}
