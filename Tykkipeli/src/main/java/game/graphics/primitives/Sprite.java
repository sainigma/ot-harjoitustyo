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
 * Implementaatio ImmediateDrawerista kuvien piirtämiseen.
 * @author Kari Suominen
 */
public class Sprite extends ImmediateDrawer {
    private Texture texture;
    private int width = -1, height = -1;
    private float[] texOffset = {0, 0};
    private float[][] vertexOffset = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
    private Vector3d origin;
    private float[][] vertices;
    private float[][] uvmap;
    
    /**
     * Täydellinen rakentaja spritelle, asettaa spritelle keskipisteen ja skaalan ja alustaa sen, lataa tekstuurin muistiin/muistista.
     * @param loader Tekstuurinlataaja
     * @param path Suhteellinen tiedostopolku assets/textures/ kansion juuresta
     * @param origin Siirtymä kuvan vasemmasta yläreunasta haluttuun keskipisteeseen
     * @param scale Skaala, 1 = 1:1 pikselisuhde
     */
    public Sprite(TextureLoader loader, String path, Vector3d origin, float scale) {
        super();
        setScale(scale);
        load(loader, path, origin);
    }
    
    /**
     * Yksinkertaistettu rakentaja spritelle, kuvan keskipiste on vasemmassa yläreunassa ja spritellä on 1:1 pikselisuhde.
     * @param loader Tekstuurinlataaja
     * @param path Suhteellinen tiedostopolku assets/textures/ kansion juuresta 
     */
    public Sprite(TextureLoader loader, String path) {
        super();
        load(loader, path, new Vector3d(0));
    }
    
    private void load(TextureLoader loader, String path, Vector3d origin) {
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
    
    /**
     * Asettaa spritelle rajauksen.
     * @param arr 
     */
    public void setCrop(int[] arr) {
        if (arr[0] > 0 && arr[1] > 0) {
            width = arr[0];
            height = arr[1];            
        }
    }
    
    /**
     * Implementaatio piirrosta, liittää tekstuurin abstraktin luokan määrittämään piirtokontekstiin ja piirtää pintalapun joka täytetään tekstuurilla.
     */
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
            float vOffset[] = vertexOffset[i];
            GL11.glTexCoord2f(point[0] - texOffset[0], point[1] - texOffset[1]);
            GL11.glVertex2f(vertex[0] - xOffset + vOffset[0], vertex[1] - yOffset + vOffset[1]);
            i += 1;
        }
        GL11.glEnd();
    }
    /**
     * Tekstuurin siirtymä verteksien määrittämän pintalapun sisällä.
     * @param v 
     */
    public void setTexOffset(float[] v) {
        texOffset[0] = v[0];
        texOffset[1] = v[1];
    }
    /**
     * Asettaa pintalapun muodon, offsettien ollessa 0 pintalappu on 1:1 neliö. Toimii hyvin neliöillä, suorakulmioilla ja suunnikkailla. Ongelmallinen puolisuunnikkaiden kanssa, aiheuttaa tarkemman näkymäprojektion puuttuessa PSX -tyylisiä affinityongelmia.
     * @param offset 
     */
    public void setVertexOffset(float[][] offset) {
        vertexOffset = offset;
    }
}
