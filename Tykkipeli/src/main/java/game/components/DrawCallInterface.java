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
 *
 * @author Kari Suominen
 */
public interface DrawCallInterface {
    public void update();
    public void load();
    public void draw();
    public void setPosition(Vector3d position);
    public void setRotation(Vector3d rotation);
    public void setGlobalPosition(Vector3d position);
    public void setGlobalRotation(Vector3d rotation);
    public void setUpdated(boolean state);
    public void setVisible(boolean state);
    public void setMinimized(boolean minimized);
    public void translate(float x, float y);
    public void translate(float x, float y, float z);
    public void setTextureLoader(TextureLoader loader);
}
