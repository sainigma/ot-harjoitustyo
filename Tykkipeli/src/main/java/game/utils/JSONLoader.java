/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 *
 * @author suominka
 */
public class JSONLoader {
    private String path;
    
    public JSONLoader(String path) {
        this.path = path;
    }
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
