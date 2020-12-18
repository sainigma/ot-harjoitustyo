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
 *  RIKKI
 * @author Kari Suominen
 */
public class BackgroundCoast extends GameObject {
    private float viewportScale;
    private float rotation = 0;
    GameObject background;
    public BackgroundCoast(String name, float viewportScale) {
        super(name);
        this.viewportScale = viewportScale;
        init();
    }
    private void init() {
        background = new GameObject("bg_foreground", "background/taustaluonnos.png", new Vector3d(0, 0, 1)) { };
        append(background);
        background.setVertexOffset(
                new float[]{0, 0},
                new float[]{0, 150f},
                new float[]{-1000f, 150f},
                new float[]{-1000f, 0}
        );
        setRotation(0);
    }
    @Override
    public void setRotation(float r) {
        float factor = (r % 360) / 360;
        background.setTexOffset(factor + 0.65f, 0);
    }
    @Override
    public void update() {
    }
}
