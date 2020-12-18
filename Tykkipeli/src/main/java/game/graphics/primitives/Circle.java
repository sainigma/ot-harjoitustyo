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

import game.graphics.VectorGraphics;
import game.utils.Vector3d;
import java.util.ArrayList;

/**
 * Jatkoimplementaatio VectorGraphicsista ympyröiden piirtämiseen.
 * @author Kari Suominen
 */
public class Circle extends VectorGraphics {
    private float step;
    
    /**
     * Rakentaja luokalle, luo verteksilistan säteen ja halutun tarkkuuden perusteella.
     * @param radius Ympyrän säde
     * @param vertices Ympyrän pisteiden määrä
     */
    public Circle(float radius, int vertices) {
        setColor(0f, 0f, 0f);
        step = (float) (Math.PI / vertices);
        createCircle(radius);
    }
    
    private void createCircle(float radius) {
        ArrayList<Vector3d> positions = new ArrayList<>();
        for (float f = 0; f < Math.PI * 2; f += step) {
            Vector3d position = new Vector3d();
            position.x = radius * Math.cos(f);
            position.y = radius * Math.sin(f);
            position.z = 0.1f;
            positions.add(position);
        }
        setVertices(positions);
    }
}
