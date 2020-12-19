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
package game.components;

import game.graphics.TextureLoader;
import game.utils.Vector3d;

/**
 * Interface objekteille joita renderöijä kutsuu jokaisella piirrolla, voi sisältää logiikkaa, piirtokutsuja tai kumpaakin.
 * @author Kari Suominen
 */
public interface DrawCallInterface {

    /**
     * Logiikan päivityskutsu.
     */
    public void update();

    /**
     * Lataa objektin muistiin.
     */
    public void load();

    /**
     * Kutsuu objektin ja sen lasten piirron.
     */
    public void draw();

    /**
     * Asettaa paikallisen sijainnin.
     * @param position
     */
    public void setPosition(Vector3d position);

    /**
     * Asettaa paikallisen kiertymän.
     * @param rotation
     */
    public void setRotation(Vector3d rotation);

    /**
     * Asettaa perityn sijainnin.
     * @param position
     */
    public void setGlobalPosition(Vector3d position);

    /**
     * Asettaa perityn kiertymän.
     * @param rotation
     */
    public void setGlobalRotation(Vector3d rotation);

    /**
     * Asettaa päivittyneisyyden tilan, käytetään ominaisuuksien propagointiin lapsiobjekteille.
     * @param state
     */
    public void setUpdated(boolean state);

    /**
     * Asettaa objektin näkyvyyden.
     * @param state
     */
    public void setVisible(boolean state);

    /**
     * Asettaa objektin pienennyksen, eroaa näkyvyydestä sillä että pienennetyllä objektilla voi olla lapsia jotka piirretään vain kun objekti on pienennetty/maksimoitu.
     * @param minimized
     */
    public void setMinimized(boolean minimized);

    /**
     * Siirtää objektia paikallisesti (summaa) kaksiulotteisesti.
     * @param x
     * @param y
     */
    public void translate(float x, float y);

    /**
     * Siirtää objektia paikallisesti (summaa) kolmiulotteisesti.
     * @param x
     * @param y
     * @param z
     */
    public void translate(float x, float y, float z);

    /**
     * Asettaa objektille tekstuurinlataajan.
     * @param loader
     */
    public void setTextureLoader(TextureLoader loader);
}
