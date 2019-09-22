package wplayer.json;

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
        catch(RuntimeException ex){
            return null;
        }
    }
    
    public static Integer getJSONInt(JSONObject JSON, String field){
        try{
            Integer result = JSON.getInt(field);
            return result;
        }
        catch(RuntimeException ex){
            return null;
        }
    }
}
