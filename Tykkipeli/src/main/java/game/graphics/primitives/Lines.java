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
 * Jatkoimplementaatio VectorGraphicsista vektorien piirtämiseen.
 * @author Kari Suominen
 */
public class Lines extends VectorGraphics {
    private boolean initialized = false;
    
    /**
     * Rakentaja.
     */
    public Lines() {
        setColor(0f, 0f, 0f);
    }
    /**
     * Onko valmis piirrettäväksi
     * @return 
     */
    public boolean isInitialized() {
        return initialized;
    }
    /**
     * Asettaa piirrettävän verteksilistan.
     * @param positions 
     */
    public void setPlot(ArrayList<Vector3d> positions) {
        setVertices(positions);
        initialized = true;
    }
}
