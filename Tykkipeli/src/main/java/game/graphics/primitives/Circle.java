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
public class Circle extends VectorGraphics {
    private float step;
    
    public Circle(float radius, int vertices) {
        setColor(0f,0f,0f);
        step = (float) (Math.PI / vertices);
        createCircle(radius);
    }
    
    private void createCircle(float radius) {
        ArrayList<Vector3d> positions = new ArrayList<>();
        for (float f = 0; f < Math.PI * 2; f += step) {
            Vector3d position = new Vector3d();
            position.x = radius * Math.cos(f);
            position.y = radius * Math.sin(f);
            position.z = 0.1f;
            positions.add(position);
        }
        setVertices(positions);
    }
}
