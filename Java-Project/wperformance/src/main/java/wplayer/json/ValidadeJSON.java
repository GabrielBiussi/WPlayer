package wplayer.json;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author petter
 */
public class ValidadeJSON {
        public static String getJSONString(JSONObject JSON, String field){
            try{
                return JSON.getString(field);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
        
        public static String getJSONString(JSONArray JSON, Integer index){
            try{
                return JSON.getString(index);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
        
        public static Boolean getJSONBoolean(JSONObject JSON, String field){
            try{
                return JSON.getBoolean(field);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
        
        public static Boolean getJSONBoolean(JSONArray JSON, Integer index){
            try{
                return JSON.getBoolean(index);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
    
        public static Integer getJSONInt(JSONObject JSON, String field){
            try{
                return JSON.getInt(field);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
        
        public static Integer getJSONInt(JSONArray JSON, Integer index){
            try{
                return JSON.getInt(index);
            }
            catch(RuntimeException ex){
                return null;
            }
        }

        public static JSONObject getJSONObject(JSONObject JSON, String field){
            try{
                return JSON.getJSONObject(field);
            }
            catch(RuntimeException ex){
                return null;
            }
        }

        public static JSONObject getJSONObject(JSONArray JSON, Integer index){
            try{
                return JSON.getJSONObject(index);
            }
            catch(RuntimeException ex){
                return null;
            }
        }

        public static JSONArray getJSONArray(JSONObject JSON, String field){
            try{
                return JSON.getJSONArray(field);
            }
            catch(RuntimeException ex){
                return null;
            }
        }

        public static JSONArray getJSONArray(JSONArray JSON, Integer index){
            try{
                return JSON.getJSONArray(index);
            }
            catch(RuntimeException ex){
                return null;
            }
        }
}
