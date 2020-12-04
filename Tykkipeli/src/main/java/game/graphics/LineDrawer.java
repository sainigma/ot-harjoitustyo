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
public class LineDrawer extends ImmediateDrawer {
    private ArrayList<Vector3d> positions;
    private boolean initialized = false;
    private int lineStep = 1;
    private float lineWidth = 1f;
    private Color color;
    
    public class Color {
        private float r;
        private float b;
        private float g;
        public Color(float r, float b, float g) {
            this.r = r;
            this.b = b;
            this.g = g;
        }
        public float[] get() {
            return new float[] {r,b,g};
        }
    }
    
    public LineDrawer() {
        setColor(0f,0f,0f);
    }
    
    public void setColor(float r, float b, float g) {
        this.color = new Color(r,b,g);
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    
    public void setPlot(ArrayList<Vector3d> positions) {
        this.positions = positions;
        initialized = true;
    }
    
    public void setLineStep(int step) {
        lineStep = step;
    }
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    @Override
    public void _draw() {
        if (positions == null) {
            return;
        }
        int i = 0;
        int j = 0;
        int size = positions.size();
        GL11.glColor3fv(color.get());
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINES);
        while (i < size) {
            Vector3d pos = positions.get(i);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
            i += lineStep;
            j += 1;
        }
        if (j % 2 != 0) {
            Vector3d pos = positions.get(size-1);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
        }
        GL11.glEnd();
    }
}
