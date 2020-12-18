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
import org.lwjgl.opengl.GL11;

/**
 * Abstrakti luokka primitiivien muuntamiseen ja piirtämiseen. Sisältää metodit piirrettävän kappaleen muunnoksen asettamiseen sekä päämetodin piirtämiseen.
 * @author Kari Suominen
 */
public abstract class ImmediateDrawer {
    private float scale = 1f;

    private Vector3d localPosition = new Vector3d(0);
    private Vector3d localRotation = new Vector3d(0);
    private Vector3d globalPosition = new Vector3d(0);
    private Vector3d globalRotation = new Vector3d(0);
    
    /**
     * Asettaa yksiulotteisen skaalan.
     * @param scale 
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    /**
     * Palauttaa yksiulotteisen skaalan.
     * @return 
     */
    public float getScale() {
        return scale;
    }
    
    private void translate(Vector3d position) {
        float inverseScale = (float) Math.pow(scale, -1);
        GL11.glTranslated(position.x * inverseScale, position.y * inverseScale, position.z * inverseScale);
    }

    private void rotate(Vector3d rotation) {
        GL11.glRotated(rotation.x, 1f, 0, 0);
        GL11.glRotated(rotation.y, 0, 1f, 0);
        GL11.glRotated(rotation.z, 0, 0, 1f);        
    }
    
    private void transform() {
        translate(globalPosition);
        rotate(globalRotation);
        translate(localPosition);
        rotate(localRotation);
    }
    /**
     * Piirtometodi implementaatiolle. Vastuussa pelkästään piirrosta.
     */
    public void drawPrimitive() {
    }
    
    /**
     * Päämetodi piirtämiseen, immediate graphics mode tyyppinen piirto. Luo uuden muunnosmatriisin piirtojonoon, asettaa sille skaalan ja muunnoksen, kutsuu implementaatiolta piirron ja poistaa matriisin piirtojonosta. 
     */
    public void draw() {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        transform();
        drawPrimitive();
        GL11.glPopMatrix();
    }
    
    /**
     * Asettaa paikallisen sijainnin.
     * @param position x: vaakasuunta, y: pystysuunta, z: syvyys
     */
    public void setPosition(Vector3d position) {
        localPosition.set(position);
    }
    
    /**
     * Asettaa paikallisen sijainnin kaksiulotteisesti.
     * @param x vaakasuunta
     * @param y pystysuunta
     */
    public void setPosition2D(float x, float y) {
        localPosition.x = 0;
        localPosition.y = 0;
    }
    
    /**
     * Liikuttaa paikallista sijaintia kaksiulotteisesti.
     * @param x
     * @param y 
     */
    public void move2D(float x, float y) {
        localPosition.x += x;
        localPosition.y += y;
    }
    
    /**
     * Asettaa perityn sijainnin.
     * @param position x: vaakasuunta, y: pystysuunta, z: syvyys
     */
    public void setGlobalPosition(Vector3d position) {
        this.globalPosition.set(position);
    }
    
    /**
     * Asettaa perityn rotaation.
     * @param rotation perityt rotaatiokomennot muunnosmatriisille, asteissa.
     */
    public void setGlobalRotation(Vector3d rotation) {
        this.globalRotation.set(rotation);
    }
    
    /**
     * Asettaa objektin täydellisen muunnoksen. Muunnos ilmaistaan kuuden vapausasteen koordinaatistolla.
     * @param localPosition paikallinen sijainti, x: vaakasuunta, y: pystysuunta, z: syvyys.
     * @param localRotation paikalliset rotaatiokomennot muunnosmatriisille, asteissa.
     * @param globalPosition peritty sijainti, x: vaakasuunta, y: pystysuunta, z: syvyys.
     * @param globalRotation perityt rotaatiokomennot muunnosmatriisille, asteissa.
     */
    public void setTransforms(Vector3d localPosition, Vector3d localRotation, Vector3d globalPosition, Vector3d globalRotation) {
        this.localPosition.set(localPosition);
        this.localRotation.set(localRotation);
        this.globalPosition.set(globalPosition);
        this.globalRotation.set(globalRotation);
    }
    
    /**
     * Asettaa objektin omistavan kappaleen muunnoksen. Muunnos ilmaistaan kuuden vapausasteen koordinaatistolla. 
     * @param globalPosition peritty sijainti, x: vaakasuunta, y: pystysuunta, z: syvyys.
     * @param globalRotation perityt rotaatiokomennot muunnosmatriisille, asteissa.
     */
    public void setGlobalTransforms(Vector3d globalPosition, Vector3d globalRotation) {
        this.globalPosition.set(globalPosition);
        this.globalRotation.set(globalRotation);
    }
}
