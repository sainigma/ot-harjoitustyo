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

import java.security.PublicKey;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Kari Suominen
 */
public class ServicesTest {
    Services services;
    
    @Before
    public void setUp() {
        services = new Services();
    }

    @Test
    public void PostWithIncorrectJSONTest() {
        String bodyWithIncorrectKey = "{\"rawr\":\"" + "adsfjlkasljfd" + "\"}";
        services.post("tykkipeli/e1m1", bodyWithIncorrectKey);
        assertTrue(services.getStatusCode() == 400);

    }
    @Test
    public void PostWithoutBodyTest() {
        String body = null;
        services.post("tykkipeli/e1m1", body);
        assertTrue(services.getStatusCode() == -1);
    }
    @Test
    public void PostWithIncorrectBodyTest() {
        String bodyWithoutKey = "alkjflkdsajflsdf";
        services.post("tykkipeli/e1m1", bodyWithoutKey);
        assertTrue(services.getStatusCode() == 400);        
    }
}
