/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author suominka
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
