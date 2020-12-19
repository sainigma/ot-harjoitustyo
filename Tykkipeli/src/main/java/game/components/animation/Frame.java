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
package game.components.animation;

/**
 * Yksittäinen animaation askel.
 * @author Kari Suominen
 */
public class Frame {

    /**
     * Askeleen tunnus, kuvaa yhtä ruudunpäivitystä.
     */
    public int frame = 0;

    /**
     * Askeleen kaksiulotteinen sijainti.
     */
    public float position[] = {0, 0};

    /**
     * Askeleen ohjausarvo.
     */
    public double value = 0;

    /**
     * Rakentaja ohjausarvoanimaation ruudulle.
     * @param frame askel, tietty ruudunpäivitys
     * @param value ohjausarvo
     */
    public Frame(int frame, double value) {
        this.frame = frame;
        this.value = value;
    }

    /**
     * Rakentaja siirtymäanimaation ruudulle.
     * @param frame askel, tietty ruudunpäivitys
     * @param position absoluuttinen sijainti
     */
    public Frame(int frame, float position[]) {
        this.frame = frame;
        this.position[0] = position[0];
        this.position[1] = position[1];
    }
}
