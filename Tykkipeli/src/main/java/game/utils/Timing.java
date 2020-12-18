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

/**
 * Ajastuksen työkalut.
 * @author Kari Suominen
 */
public class Timing {
    private long lastTime;
    private double timer;
    
    /**
     * Rakentaja.
     */
    public Timing() {
        lastTime = System.nanoTime() / 1000000;
        timer = 0;
    }
    
    /**
     * Päivittää ajastimen ja palauttaa edellisen ja nykyisen päivityksen välisen ajan.
     * @return päivitykseen kulunut aika millisekunneissa
     */
    public double getDeltatimeMillis() {
        double deltatimeMillis = 0;
        long time = System.nanoTime() / 1000000;
        if (lastTime > 0) {
            deltatimeMillis = (double) (time - lastTime);
        }
        timer += deltatimeMillis;
        lastTime = time;
        return deltatimeMillis;
    }
    /**
     * Palauttaa kokonaisajastimen arvon.
     * @return ajastus millisekunneissa
     */
    public double getTimer() {
        return timer;
    }
    /**
     * Nollaa ajastimen.
     */
    public void resetTimer() {
        timer = 0;
    }
}
