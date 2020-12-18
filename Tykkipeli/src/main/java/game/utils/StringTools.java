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
 * Merkkijonojen työkalut.
 * @author Kari Suominen
 */
public class StringTools {
    
    /**
     * Muuttaa kokonaisluvun tietyn pituiseksi merkkijonoksi, tyhjät kohdat korvataan nollille.
     * @param points
     * @param maxLength
     * @return 
     */
    public String padZeros(int points, int maxLength) {
        return padZeros(Integer.toString(points), maxLength);
    }
    
    /**
     * Muuttaa merkkijonon tietyn pituiseksi, tyhjät kohdat korvataan nollilla.
     * @param points
     * @param maxLength
     * @return 
     */
    public String padZeros(String points, int maxLength) {
        String zeros = "";
        for (int i = points.length(); i < maxLength; i++) {
            zeros += "0";
        }
        return zeros + points;
    }
}
