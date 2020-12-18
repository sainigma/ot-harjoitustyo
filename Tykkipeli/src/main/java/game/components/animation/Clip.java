/*
 * Copyright (C) 2020 Kari Suominen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package game.components.animation;

import game.components.animation.Frame;
import java.util.ArrayList;

/**
 *
 * @author Kari Suominen
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