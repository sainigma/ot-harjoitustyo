/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.graphics.primitives;

import game.graphics.VectorGraphics;
import game.utils.Vector3d;
import java.util.ArrayList;

/**
 *
 * @author suominka
 */
public class Lines extends VectorGraphics {
    private boolean initialized = false;
    
    public Lines() {
        setColor(0f, 0f, 0f);
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public void setPlot(ArrayList<Vector3d> positions) {
        setVertices(positions);
        initialized = true;
    }
}
