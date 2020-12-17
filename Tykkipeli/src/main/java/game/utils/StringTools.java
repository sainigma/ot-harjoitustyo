/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.utils;

/**
 *
 * @author suominka
 */
public class StringTools {

    public String padZeros(int points, int maxLength) {
        return padZeros(Integer.toString(points), maxLength);
    }

    public String padZeros(String points, int maxLength) {
        String zeros = "";
        for (int i = points.length(); i < maxLength; i++) {
            zeros += "0";
        }
        return zeros + points;
    }
}
