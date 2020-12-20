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
package game.components.templates;

import game.components.GameObject;
import game.utils.Vector3d;

/**
 * Implementaatio peliobjektista pelin taustan luomiseen.
 * @author Kari Suominen
 */
public class BackgroundCoast extends GameObject {
    private float rotation = 0;
    private GameObject background;
    /**
     * Rakentaja
     */
    public BackgroundCoast() {
        super("backgroundCoast");
        init();
    }
    private void init() {
        background = new GameObject("bg_foreground", "background/taustaluonnos.png", new Vector3d(0, 0, 1), 0.666f) { };
        background.translate(0, -50);
        append(background);
        setRotation(0);
    }
    /**
     * Asettaa toistuvan taustatekstuurin siirtymän kiertymän perusteella, 360 asteen kiertymä saa aikaan täydellisen vaakasuuntaisen siirtymän. 
     * @param r kiertymä asteissa
     */
    @Override
    public void setRotation(float r) {
        float factor = (r % 360) / 360;
        background.setTexOffset(factor + 0.65f, 0);
    }
}
