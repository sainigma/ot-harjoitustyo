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
package game.simulations.cases;
import game.simulations.PhysicsSolver;
import game.utils.Vector3d;

/**
 * Yksinkertainen implementaatio ratkaisijasta paraboliselle lentoradalle, testikäyttöön.
 * @author Kari Suominen
 */
public class Parabola extends PhysicsSolver {
    /**
     * Rakentaja, asettaa alkuarvoksi 45 asteen suunnan sekä tarkemman simulaatioaskeleen.
     */
    public Parabola() {
        super();
        set(new Vector3d(), new Vector3d(100, 100, 0), 0.0001);
    }
}
