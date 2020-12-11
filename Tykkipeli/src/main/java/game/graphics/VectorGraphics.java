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
abstract public class VectorGraphics extends ImmediateDrawer {
    private ArrayList<Vector3d> vertices;
    private Color color;
    private int lineStep = 1;
    private float lineWidth = 1f;
    
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
            return new float[] {r, b, g};
        }
    }
    
    public void setVertices(ArrayList<Vector3d> vertices) {
        this.vertices = vertices;
    }
    
    public void setColor(float r, float b, float g) {
        this.color = new Color(r, b, g);
    }
    
    public float[] getColor() {
        return color.get();
    }
    
    public void setLineStep(int step) {
        lineStep = step;
    }
    public int getLineStep() {
        return lineStep;
    }
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    public float getLineWidth() {
        return lineWidth;
    }
    private void drawLines() {
        int i = 0;
        int j = 0;
        int size = vertices.size();
        
        while (i < size) {
            Vector3d pos = vertices.get(i);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
            i += getLineStep();
            j += 1;
        }
        if (j % 2 != 0) {
            Vector3d pos = vertices.get(size - 1);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
        }
    }
    @Override
    public void _draw() {
        if (vertices == null) {
            return;
        }
        GL11.glColor3fv(getColor());
        GL11.glLineWidth(getLineWidth());
        GL11.glBegin(GL11.GL_LINES);
        drawLines();
        GL11.glEnd();
    }
}
