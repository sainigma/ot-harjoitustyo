/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.graphics.primitives;

import game.graphics.ImmediateDrawer;
import game.graphics.Texture;
import game.graphics.TextureLoader;
import game.utils.Vector3d;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author suominka
 */
public class Sprite extends ImmediateDrawer {
    Texture texture;
    int width = -1, height = -1;
    float[] texOffset = {0, 0};
    float[][] vertexOffset = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
    Vector3d origin;
    private float[][] vertices;
    private float[][] uvmap;
    
    private void _load(TextureLoader loader, String path, Vector3d origin) {
        texture = loader.loadTexture("./assets/textures/" + path);
        if (width == -1) {
            width = texture.getImageWidth();
            height = texture.getImageHeight();
        }
        float [][] v = {{0, 0}, {0, height}, {width, height}, {width, 0}};
        float [][] u = {{0, 0}, {0, 1}, {1, 1}, {1, 0}};
        vertices = v;
        uvmap = u;
        this.origin = origin;
    }
    
    public void setCrop(int[] arr) {
        if (arr[0] > 0 && arr[1] > 0) {
            width = arr[0];
            height = arr[1];            
        }
    }
    
    public Sprite(TextureLoader loader, String path, Vector3d origin, float scale) {
        super();
        setScale(scale);
        _load(loader, path, origin);
    }

    public Sprite(TextureLoader loader, String path) {
        super();
        _load(loader, path, new Vector3d(0));
    }
    
    @Override
    public void _draw() {
        int i = 0;
        float xOffset = (float) origin.x;
        float yOffset = (float) origin.y;
        texture.bind();
        GL11.glColor3f(1, 1, 1);
        GL11.glBegin(GL11.GL_QUADS);
        for (float[] vertex : vertices) {
            float point[] = uvmap[i];
            float vOffset[] = vertexOffset[i];
            GL11.glTexCoord2f(point[0] - texOffset[0], point[1] - texOffset[1]);
            GL11.glVertex2f(vertex[0] - xOffset + vOffset[0], vertex[1] - yOffset + vOffset[1]);
            i += 1;
        }
        GL11.glEnd();
    }

    public void setTexOffset(float[] v) {
        texOffset[0] = v[0];
        texOffset[1] = v[1];
    }
    public void setVertexOffset(float[][] offset) {
        vertexOffset = offset;
    }
}
