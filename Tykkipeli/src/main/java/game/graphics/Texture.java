/*
 * Copyright (c) 2002-2010 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package game.graphics;
import static org.lwjgl.opengl.GL11.*;

/**
 * Tekstuuriobjektin luokka. Jokainen moottorin käyttämä yksittäinen kuva sidotaan yhteen tekstuuriin jota voi kutsua piirrettäväksi id:n perusteella. Käyttää pohjana LWJGL:n SpaceInvadersin esimerkin <a href="http://wiki.lwjgl.org/wiki/Examples:SpaceInvaders_Texture.html">Texture</a> -luokkaa.
 * @author Kari Suominen, 
 * @author Kevin Glass
 * @author Brian Matzon
 */

public class Texture { 
    private int target; 
    private int textureID;
    private int height;
    private int width;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;
 
    /**
     * Rakentaja tekstuurille.
     * @param target Piirtotyyppi, yleensä GL_TEXTURE_2D
     * @param textureID Tekstuurin id
     */
    public Texture(int target, int textureID) {
        this.target = target;
        this.textureID = textureID;
    }
    /**
     * Liittää tekstuurin käynnissä olevaan piirtokontekstiin, kutsutaan jokaiselle spritelle jokaisen piirron yhteydessä.
     */
    public void bind() {
        glBindTexture(target, textureID);
    }
    /**
     * Asettaa kuvan leveyden pikseleissä.
     * @param width 
     */
    public void setWidth(int width) {
        this.width = width;
        setWidth();
    }
    /**
     * Asettaa kuvan korkeuden pikseleissä.
     * @param height 
     */
    public void setHeight(int height) {
        this.height = height;
        setHeight();
    }
    /**
     * Asettaa tekstuurin leveyden pikseleissä, yleensä kahden potenssi.
     * @param texWidth 
     */
    public void setTextureWidth(int texWidth) {
        this.texWidth = texWidth;
        setWidth();
    }
    /**
     * Asettaa tekstuurin korkeuden pikseleissä, yleensä kahden potenssi.
     * @param texHeight 
     */
    public void setTextureHeight(int texHeight) {
        this.texHeight = texHeight;
        setHeight();
    }
    /**
     * Asettaa tekstuurin suhteellisen leveyden, kahden potenssin kokoisilla tekstuureilla asettaa kooksi 1.
     */
    private void setWidth() {
        if (texWidth != 0) {
            widthRatio = ((float) width) / texWidth;
        }
    }
    /**
     * Asettaa tekstuurin suhteellisen korkeuden, kahden potenssin kokoisilla tekstuureilla asettaa kooksi 1.
     */
    private void setHeight() {
        if (texHeight != 0) {
            heightRatio = ((float) height) / texHeight;
        }
    }
    /**
     * Palauttaa tekstuurin leveyden pikseleissä.
     * @return 
     */
    public int getImageWidth() {
        return width;
    }
    /**
     * Palauttaa tekstuurin korkeuden pikseleissä.
     * @return 
     */
    public int getImageHeight() {
        return height;
    }
    /**
     * Palauttaa tekstuurin suhteellisen leveyden.
     * @return välillä 0-1f
     */
    public float getWidth() {
        return widthRatio;
    }
    /**
     * Palauttaa tekstuurin suhteellisen korkeuden.
     * @return välillä 0-1f
     */
    public float getHeight() {
        return heightRatio;
    }
}