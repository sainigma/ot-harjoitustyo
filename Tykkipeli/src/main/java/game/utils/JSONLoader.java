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

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * Tiedostonlukija JSON -tiedostoille.
 * @author Kari Suominen
 */
public class JSONLoader {
    private String path;
    
    /**
     * Rakentaja, asettaa pohjapolun.
     * @param path 
     */
    public JSONLoader(String path) {
        this.path = path;
    }
    /**
     * Lukee ja palauttaa .json tiedoston JSONObject muodossa.
     * @param file suhteellinen polku ilman pohjapolkua ja tiedostomuotoa
     * @return 
     */
    public JSONObject read(String file) {
        try {
            String raw = new String(Files.readAllBytes(Paths.get(path + file + ".json")), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(raw);
            return obj;
        } catch (Exception e) {
            System.out.println(e + ": file read error");
            return null;
        }
    }
    /**
     * Vastaanottaa JSONObjectin ja tallentaa sen .json tiedostona.
     * @param object
     * @param file suhteellinen polku ilman pohjapolkua ja tiedostomuotoa
     */
    public void save(JSONObject object, String file) {
        if (object == null) {
            return;
        }
        try {
            FileWriter fWriter = new FileWriter(path + file + ".json");
            fWriter.write(object.toString());
            fWriter.close();
        } catch (Exception e) {
            System.out.println(e + ": file write error");
        }
    }
}
