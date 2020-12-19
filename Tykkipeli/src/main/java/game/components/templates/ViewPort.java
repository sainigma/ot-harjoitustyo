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

import game.components.DrawCallInterface;
import game.utils.Vector3d;
import game.components.GameObject;
import java.util.Random;

/**
 * World-tyyppinen peliobjekti näkymien toteuttamiseen. Käyttäytyy kuten empty, mutta toteuttaa näkymän pienennyksen ja tärisytyksen.
 * @author Kari Suominen
 */
public class ViewPort extends GameObject {
    private boolean screenShake = false;
    private float screenShakeIntensity = 1;
    private boolean minimized = false;
    
    private Random rand = new Random();
    
    /**
     * Rakentaja, alustaa emptyn.
     * @param name
     */
    public ViewPort(String name) {
        super(name);
    }
    
    /**
     * Päivitysmetodi, tärisyttää näkymää ja sen kaikkia lapsiobjekteja jos tärisytys on käynnissä.
     */
    @Override
    public void update() {
        if (screenShake) {
            setPosition(new Vector3d(
                    (rand.nextFloat() * 4 * screenShakeIntensity),
                    (rand.nextFloat() * 2 * screenShakeIntensity)
            ));
        }
    }

    /**
     * Asettaa tärisytyksen arvon, nollaa näkymän ja sen lapset jos tärinä on loppu.
     * @param intensity
     */
    public void setScreenShake(float intensity) {
        screenShakeIntensity = intensity;
        if (intensity < 0.1) {
            screenShake = false;
            setPosition(new Vector3d());
        } else {
            screenShake = true;
        }
    }

    /**
     * Palauttaa toden jos näkymä tärisee.
     * @return
     */
    public boolean isShaking() {
        return screenShake;
    }

    /**
     * Palauttaa toden jos näkymä on pienennetty.
     * @return
     */
    @Override
    public boolean isMinimized() {
        return minimized;
    }

    /**
     * Kytkee näkymän pienennyksen päälle ja pois.
     */
    @Override
    public void toggleMinimized() {
        setMinimized(!minimized);
    }

    /**
     * Asettaa näkymän pienennyksen suoraan.
     * @param state
     */
    @Override
    public void setMinimized(boolean state) {
        minimized = state;
        for (DrawCallInterface child : children) {
            child.setMinimized(minimized);
        }
    }
}
