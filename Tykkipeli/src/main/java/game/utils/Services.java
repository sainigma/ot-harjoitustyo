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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kari Suominen
 */
public class Services {
    private String basepath = "http://192.168.0.100/api/";
    private int timeoutMillis = 1000;
    
    public int post(String path, String body) {
        HttpClient client = HttpClient.newHttpClient();
        URI serverURI = URI.create(basepath + path);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(serverURI)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofMillis(timeoutMillis))
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res.statusCode();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");
            return -1;
        }
    }
    
    public HttpResponse<String> get(String path) {
        HttpClient client = HttpClient.newHttpClient();
        URI serverURI = URI.create(basepath + path);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(serverURI)
                .header("Accept", "application/json")
                .GET()
                .timeout(Duration.ofMillis(timeoutMillis))
                .build();
        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            return res;
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");            
        }
        return null;
    }
    
    public JSONArray getJSONArray(String path) {
        HttpResponse<String> res = get(path);
        JSONArray array = new JSONArray(res.body());
        return array;
    }
    
    public JSONObject getJSONObject(String path) {
        HttpResponse<String> res = get(path);
        if (res != null) {
            JSONObject object = new JSONObject(res.body());
            return object;
        }
        return null;
    }
}
