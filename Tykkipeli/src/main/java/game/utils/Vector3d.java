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
package game.utils;

import static java.lang.Math.sqrt;

/**
 * Kolmiulotteinen vektori ja yleiset vektorityökalut.
 * @author Kari Suominen
 */
public class Vector3d {
    /**
     * Vektorin ulottuvuus.
     */
    public double x, y, z;
    /**
     * Rakentaja nollavektorille.
     */
    public Vector3d() {
        x = 0;
        y = 0;
        z = 0;
    }
    /**
     * Täydellinen rakentaja vektorille.
     * @param x
     * @param y
     * @param z 
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * Rakentaja kaksiulotteiselle vektorille.
     * @param x
     * @param y 
     */
    public Vector3d(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }
    /**
     * Rakenteja vektorille jonka kaikki ulottuvuudet ovat samoja.
     * @param x 
     */
    public Vector3d(double x) {
        this.x = x;
        this.y = x;
        this.z = x;
    }
    /**
     * Palauttaa vektorin suuruuden.
     * @return 
     */
    public double magnitude() {
        return sqrt(x * x + y * y + z * z);
    }
    /**
     * Kloonaa vektorin.
     * @return 
     */
    @Override
    public Vector3d clone() {
        return new Vector3d(x, y, z);
    }
    /**
     * Palauttaa uuden vektorin joka on nykyisen ja toisen vektorin summa.
     * @param b
     * @return 
     */
    public Vector3d add(Vector3d b) {
        return new Vector3d(this.x + b.x, this.y + b.y, this.z + b.z);
    }
    /**
     * Palauttaa uuden vektorin joka on nykyisen ja toisen vektorin vähennys.
     * @param b
     * @return 
     */
    public Vector3d diff(Vector3d b) {
        return new Vector3d(x - b.x, y - b.y, z - b.z);
    }
    /**
     * Palauttaa uuden skalaarilla kerrotun vektorin.
     * @param s
     * @return 
     */
    public Vector3d scale(double s) {
        return new Vector3d(this.x * s, this.y * s, this.z * s);
    }
    /**
     * Palauttaa uuden vektorin joka on lineaari-interpoloitu lähtö- ja loppuvektorista kertoimella t.
     * @param a Lähtövektori
     * @param b Loppuvektori
     * @param t Siirtymäkerroin
     * @return 
     */
    public Vector3d lerp(Vector3d a, Vector3d b, float t) {
        return a.scale(1 - t).add(b.scale(t));
    }
    /**
     * Pyörittää vektoria X-akselin ympäri.
     * @param angle asteissa
     */
    public void rotateX(double angle) {
        double rads = deg2Rad(angle);
        double y0 = y;
        double z0 = z;
        y = y0 * Math.cos(rads) - z0 * Math.sin(rads);
        z = y0 * Math.sin(rads) + z0 * Math.cos(rads);
    }
    /**
     * Pyörittää vektoria Y-akselin ympäri.
     * @param angle asteissa
     */
    public void rotateY(double angle) {
        double rads = deg2Rad(angle);
        double x0 = x;
        double z0 = z;
        x = x0 * Math.cos(rads) + z0 * Math.sin(rads);
        z = -x0 * Math.sin(rads) + z0 * Math.cos(rads);
    }
    /**
     * Pyörittää vektoria Z-akselin ympäri.
     * @param angle asteissa
     */
    public void rotateZ(double angle) {
        double rads = deg2Rad(angle);
        double x0 = x;
        double y0 = y;
        x = x0 * Math.cos(rads) - y0 * Math.sin(rads);
        y = x0 * Math.sin(rads) + y0 * Math.cos(rads);
    }
    private double deg2Rad(double degs) {
        return (degs / 360f) * Math.PI * 2f;
    }
    /**
     * Muuntaa vektorin yksikkövektoriksi.
     */
    public void normalize() {
        double factor = 1f / magnitude();
        set(this.scale(factor));
    }
    /**
     * Asettaa vektorin toisella vektorilla.
     * @param toCopy 
     */
    public void set(Vector3d toCopy) {
        x = toCopy.x;
        y = toCopy.y;
        z = toCopy.z;
    }
    /**
     * Muuntaa vektorin yksikkövektoriksi joka osoittaa pallokoordinaattiarvojen määräämään suuntaan.
     * @param azimuth
     * @param altitude 
     */
    public void setByAzimuthAltitude(double azimuth, double altitude) {
        x = 1;
        y = 0;
        z = 0;
        rotateZ(altitude);
        rotateY(azimuth);
    }
    /**
     * Palauttaa vektorin arvot tekstimuodossa, testikäyttöön.
     * @return 
     */
    @Override
    public String toString() {
        return "x: " + x + ", y: " + y + ", z: " + z;
    }
}
