/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import org.json.JSONObject;

/**
 *
 * @author petter
 */
public class ValidadeJSON {
        public static String getJSONString(JSONObject JSON, String field){
        try{
            String result = JSON.getString(field);
            return result;
        }
        catch(RuntimeException e){
            return null;
        }
    }
    
    public static Integer getJSONInt(JSONObject JSON, String field){
        try{
            Integer result = JSON.getInt(field);
            return result;
        }
        catch(RuntimeException e){
            return null;
        }
    }
}
