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
 * Työkalut json-objektien vastaanottamiseen ja lähettämiseen pelin backendin REST-rajapintaan.
 * @author Kari Suominen
 */
public class Services {
    private String basepath = "https://1030321.xyz/api/";
    private int timeoutMillis = 1000;
    private int statusCode = 0;
    /**
     * Lähettää merkkijonoksi muutetun json-objektin POST pyyntönä.
     * @param path suhteellinen polku rajapintaan
     * @param body lähetettävä objekti
     * @return 
     */
    public int post(String path, String body) {
        statusCode = -1;
        if (body == null || body.isBlank()) {
            return statusCode;
        }
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
            statusCode = res.statusCode();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");
        }
        return statusCode;
    }
    
    private HttpResponse<String> get(String path) {
        statusCode = -1;
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
            statusCode = res.statusCode();
            return res;
        } catch (IOException | InterruptedException ex) {
            System.out.println("Connection to server failed: timeout");
        }
        return null;
    }
    
    /**
     * Hakee listamuotoisen JSON-objektin rajapinnasta.
     * @param path suhteellinen osoite rajapintaan
     * @return 
     */
    public JSONArray getJSONArray(String path) {
        HttpResponse<String> res = get(path);
        JSONArray array = new JSONArray(res.body());
        return array;
    }
    
    /**
     * Hakee geneerisen JSON-objektin rajapinnasta.
     * @param path suhteellinen osoite rajapintaan
     * @return 
     */
    public JSONObject getJSONObject(String path) {
        HttpResponse<String> res = get(path);
        if (res != null) {
            JSONObject object = new JSONObject(res.body());
            return object;
        }
        return null;
    }
    /**
     * Palauttaa viimesimmän tapahtuman statuskoodin.
     * @return 
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Asettaa juuripolun, testauskäyttöön.
     * @param path 
     */
    public void setPath(String path) {
        basepath = path;
    }
}
