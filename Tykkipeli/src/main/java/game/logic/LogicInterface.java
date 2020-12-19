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
package game.logic;

import game.graphics.Renderer;
import game.utils.InputManager;

/**
 * Interface ohjauslogiikalle, toimii wrapperina sekä määrittää ohjauslogiikan pakolliset metodit.
 * @author Kari Suominen
 */
public interface LogicInterface {

    /**
     * Pelissä on vain yksi näppäimistökuuntelija, logiikka toteuttaa käyttöliittymän ohjauksen sillä. Kuuntelija jaetaan eteenpäin seuraavaa logiikkaa spawnatessa.
     * @param inputs
     */
    public void setInputManager(InputManager inputs);
    /**
     * Pelissä on vain yksi renderöijä, viittaus renderöijään tarvitaan että logiikka voi lisätä ja poistaa objekteja renderöijän piirtojonosta. Renderöijä jaetaan eteenpäin seuraavaa logiikkaa spawnatessa, renderöijälle myös asetetaan viittaus aktiiviseen logiikkaan.
     * @param renderer 
     */
    public void setRenderer(Renderer renderer);
    /**
     * Päivitysmetodi interfacelle, kutsutaan jokaisella ruudunpiirrolla renderöijästä.
     */
    public void update();
}
