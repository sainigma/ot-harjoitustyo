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
package game.graphics;

import game.utils.Vector3d;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

/**
 * Abstrakti luokka vektorigrafiikan piirtämiseen. Jatkaa ImmediateDraweria.
 * @author Kari Suominen
 */
abstract public class VectorGraphics extends ImmediateDrawer {
    private ArrayList<Vector3d> vertices;
    private Color color;
    private int lineStep = 1;
    private float lineWidth = 1f;
    
    private class Color {
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
    
    /**
     * Asettaa verteksit joiden perusteella vektorigrafiikka luodaan.
     * @param vertices 
     */
    public void setVertices(ArrayList<Vector3d> vertices) {
        this.vertices = vertices;
    }
    
    /**
     * Asettaa piirrettävän linjan värin 0-1f skaalalla.
     * @param r
     * @param b
     * @param g 
     */
    public void setColor(float r, float b, float g) {
        this.color = new Color(r, b, g);
    }
    /**
     * Määrittää kuinka tarkasti vektorit piirretään.
     * @param step Joka n:nnes. 1 = kaikki piirretään
     */
    public void setLineStep(int step) {
        lineStep = step;
    }
    /**
     * Asettaa linjapaksuuden.
     * @param lineWidth 1 = 1px leveys. Askellus näytönohjaimesta riippuvainen.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    private void drawLines() {
        int i = 0;
        int j = 0;
        int size = vertices.size();
        
        while (i < size) {
            Vector3d pos = vertices.get(i);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
            i += lineStep;
            j += 1;
        }
        if (j % 2 != 0) {
            Vector3d pos = vertices.get(size - 1);
            GL11.glVertex3d(pos.x, pos.y, -pos.z);
        }
    }

    /**
     * Implementoitu piirtometodi. Asettaa lisäparametrit vektoripiirtoa varten ja kutsuu sisäisen piirtometodin.
     */
    @Override
    public void drawPrimitive() {
        if (vertices == null) {
            return;
        }
        GL11.glColor3fv(color.get());
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINES);
        drawLines();
        GL11.glEnd();
    }
}
