/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.components.animation;

import game.components.animation.Frame;
import java.util.ArrayList;

/**
 *
 * @author suominka
 */
public class Clip {
    private int current = 0;
    private int next = 1;
    private boolean linearInterpolation = true;
    private ArrayList<Frame> frames;

    public Clip() {
        frames = new ArrayList<>();
    }

    private double lerpValue(double a, double b, double factor) {
        return (1 - factor) * a + factor * b;
    }
    
    private Frame getFrameLinear(int frame) {
        if (frames.get(next) == frames.get(current)) {
            return getFrameConstant();
        }
        double factor = (double) (frame - frames.get(current).frame) / (double) (frames.get(next).frame - frames.get(current).frame);
        double lerp = lerpValue(frames.get(current).value, frames.get(next).value, factor);
        return new Frame(0, lerp);
    }
    
    public void setLinearInterpolation(boolean state) {
        linearInterpolation = state;
    }
    
    private Frame getFrameConstant() {
        if (current < frames.size()) {
            return frames.get(current);
        }
        return frames.get(frames.size() - 1);
    }

    public void reset() {
        current = 0;
        next = 1;
    }

    private void advance(int frame) {
        if (next + 1 < frames.size()) {
            if (frames.get(next).frame < frame) {
                current += 1;
                next += 1;
            }
        } else {
            current = frames.size() - 1;
            next = current;
        }
    }
    
    public Frame getFrame(int frame) {
        advance(frame);
        if (linearInterpolation) {
            return getFrameLinear(frame);
        }
        return getFrameConstant();
    }
    public void addFrame(Frame frame) {
        frames.add(frame);
    }
}