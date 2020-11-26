/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components;

/**
 *
 * @author suominka
 */
public class Frame {
    public int frame = 0;
    public float position[] = {0, 0};
    public double value = 0;

    public Frame(int frame, double value) {
        this.frame = frame;
        this.value = value;
    }

    public Frame(int frame, float position[]) {
        this.frame = frame;
        this.position[0] = position[0];
        this.position[1] = position[1];
    }
}
