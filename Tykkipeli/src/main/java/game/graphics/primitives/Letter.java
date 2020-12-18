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
package game.graphics.primitives;

import game.graphics.ImmediateDrawer;
import game.graphics.Texture;
import game.graphics.TextureLoader;
import game.utils.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Kari Suominen
 */
public class Letter extends ImmediateDrawer {
    Texture texture;
    int width = -1, height = -1;
    int letterWidth;
    private float[][] vertices;
    private float[][] uvmap;
    private float[] texOffset = {0, 0};
    Vector3d origin;
    
    public Letter(TextureLoader loader, String fontName) {
        load(loader, fontName);
    }
    
    private void load(TextureLoader loader, String fontName) {
        texture = loader.loadTexture("./assets/textures/fonts/" + fontName + ".png");
        width = texture.getImageWidth();
        height = texture.getImageHeight();
        letterWidth = (int) (width / 16f);
        float [][] v = {{0, 0}, {0, letterWidth}, {letterWidth, letterWidth}, {letterWidth, 0}};
        float [][] u = {{0, 0}, {0, 1 / 16f}, {1f / 16f, 1 / 16f}, {1f / 16f, 0}};
        vertices = v;
        uvmap = u;
        origin = new Vector3d();
        //origin = new Vector3d(width / 2, height, 100);
    }
    
    @Override
    public void drawPrimitive() {
        int i = 0;
        float xOffset = (float) origin.x;
        float yOffset = (float) origin.y;
        texture.bind();
        GL11.glColor3f(1, 1, 1);
        GL11.glBegin(GL11.GL_QUADS);
        for (float[] vertex : vertices) {
            float point[] = uvmap[i];
            GL11.glTexCoord2f(point[0] - texOffset[0], point[1] - texOffset[1]);
            GL11.glVertex2f(vertex[0] - xOffset, vertex[1] - yOffset);
            i += 1;
        }
        GL11.glEnd();
    }
    
    public void setIndex(int i) {
        texOffset[0] = -(1 / 16f) * (i % 16);
        texOffset[1] = -(1 / 16f) * (i / 16);
    }

    public void reset() {
        setPosition2D(0, 0);
    }
    
    public void setPosition(int spaces, float lineChanges) {
        move2D(spaces * 16f, lineChanges * 16f);
    }
}
