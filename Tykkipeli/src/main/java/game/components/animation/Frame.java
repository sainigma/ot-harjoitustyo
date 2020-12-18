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
 *
 * @author Kari Suominen
 */
public class Frame {
    public int frame = 0;
    public float position[] = {0, 0};
    public double value = 0;

    public Frame(int frame, double value) {
        this.frame = frame;
        this.value = value;
    }

    public Frame(int frame, float position[]) {
        this.frame = frame;
        this.position[0] = position[0];
        this.position[1] = position[1];
    }
}
