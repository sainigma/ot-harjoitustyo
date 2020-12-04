/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.graphics;

import game.utils.Vector3d;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author suominka
 */
public class Plotter extends ImmediateDrawer {
    private ArrayList<Vector3d> positions;
    
    public void setPositions(ArrayList<Vector3d> positions) {
        this.positions = positions;
    }
    
    @Override
    public void _draw() {
        if (positions == null) {
            System.out.println("en piirrä");
            return;
        }
        int step = 10;
        int i = 0;
        int size = positions.size();
        GL11.glColor3f(0, 0, 0);
        GL11.glBegin(GL11.GL_LINES);
        while (i < size) {
            Vector3d pos = positions.get(i);
            System.out.println(pos);
            GL11.glVertex3d(pos.x, pos.y, pos.z);
            i += step;
        }
        GL11.glEnd();
    }
}
