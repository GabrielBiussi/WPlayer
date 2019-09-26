package wplayer.steam.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.database.DBQuery;
import wplayer.json.ValidadeJSON;

/**
 *
 * @author petter
 */
public class SteamGames {
    private static final String ENDPOINTAPPS = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
    private static final String ENDPOINTDETAILS = "https://store.steampowered.com/api/appdetails/?appids=";
    private static final String FIELD = "APP_ID";
    private static final String TABLE = "APPS";
    
    public static void updateTableGames(){
        Map<String, Boolean> allApps = getAppsDictionary();
        String insertQuery = "INSERT INTO "+TABLE+" VALUES ";
        int count = 0;
        
        for (String appId : allApps.keySet()){
            if(allApps.get(appId)){
                String str = getFieldsDetails(appId, true);
                System.out.println(str);
                
                if(str != null)
                    updateRow(str);
            } else{
                String str = getFieldsDetails(appId, false);
                System.out.println(str);
                if(str != null){
                insertQuery += str;
               
                count++;
                }
                if(count > 30){
                    count = 0;
                    System.out.println("ELE CHEGA");
                    insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
                    insertRows(insertQuery);
                    insertQuery = "INSERT INTO "+TABLE+" VALUES ";
                }
            }
            
        }
        
        if(count > 0){
                insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
                insertRows(insertQuery);
        }
        System.out.println("CHEGA TBM");
    }    
    private static void insertRows(String insertQuery){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            statement.execute(insertQuery);
        } catch (SQLException ex) {
            System.err.println("Erro (SteamGames.insertRows): "+insertQuery
                             + " LOG: " +ex);
        } finally{
            DBConnection.closeConnection(connection, statement);
        }
    }
    
    private static void updateRow(String sqlStatement){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            
            statement.executeUpdate(sqlStatement);
        } catch (SQLException ex) {
            System.err.println("Erro (SteamGames.updateRow): "+sqlStatement
                             + " LOG: " +ex);
        } finally{
            DBConnection.closeConnection(connection, statement);
        }
    }
    
    public static String getFieldsDetails(String id, Boolean isUpdate){
        try {
            JSONObject fullDetails = RequestAPI.getJSON(ENDPOINTDETAILS+id);
            
            JSONObject refinedDetails = fullDetails.getJSONObject(id);
            
            if(ValidadeJSON.getJSONBoolean(refinedDetails, "success")){
                JSONObject data = ValidadeJSON.getJSONObject(refinedDetails, "data");
                JSONObject fullGame = ValidadeJSON.getJSONObject(data, "fullgame");
                
                JSONArray fullCategories = ValidadeJSON.getJSONArray(data, "categories");
                JSONArray fullGenres = ValidadeJSON.getJSONArray(data, "genres");
                
                String nameApp = ValidadeJSON.getJSONString(data, "name");
                String type = ValidadeJSON.getJSONString(data, "type");
                String fullGameID = ValidadeJSON.getJSONString(fullGame, "appid");
                String genres = getConcateGenres(fullGenres);
                
                Integer requiredAge = ValidadeJSON.getJSONInt(data, "required_age");
                Integer isFree = ValidadeJSON.getJSONBoolean(data, "is_free")? 1 : 0;
                Integer isMultiplayer = isMultiplayer(fullCategories)? 1 : 0;
                Integer isSinglePlayer = isSinglePlayer(fullCategories)? 1 : 0;
                
                if(isUpdate){
                    return String.format("UPDATE %s "
                                          + "SET APP_NAME = '%s',"
                                           + "APP_TYPE = '%s',"
                                           + "APP_REQUIRED_AGE = %s,"
                                           + "APP_MAIN_GAME_ID = '%s',"
                                           + "APP_GENRES = '%s',"
                                           + "APP_IS_FREE = %s,"
                                           + "APP_IS_MULTIPLAYER = %s,"
                                           + "APP_IS_SINGLEPLAYER = %s "
                                        + "WHERE APP_ID = '%s'",
                        TABLE, nameApp, type, requiredAge, fullGameID, genres,
                        isFree, isMultiplayer, isSinglePlayer, id);
                }
                else{
                    return String.format("('%s','%s','%s',%s,"
                                       + "'%s','%s',%s,%s,%s),",
                        id, nameApp, type, requiredAge, fullGameID, genres,
                        isFree, isMultiplayer, isSinglePlayer);
                }
            }
            else{
                System.err.println("Erro na API p/ Aquisição"
                                 + " de campos do appId: "+id);
            }
            
            return null;
            
        } catch (IOException ex) {
            System.err.println("Erro (SteamGames.getFieldsDetails): " +ex);
        }
        
        return null;
    }
    
    private static Map<String, Boolean> getAppsDictionary(){
        return DBQuery.hasRowsInTable(getAppsId(), FIELD, TABLE);
    }
    
    private static ArrayList<String> getAppsId(){ 
            
        ArrayList<String> idApps  = new ArrayList<>();
            
        getAppsList().forEach(g -> {
            JSONObject game = (JSONObject) g;
            idApps.add(ValidadeJSON.getJSONInt(game, "appid").toString());
        });
            
        return idApps;
    }
    
    private static JSONArray getAppsList(){
        try{
            JSONObject fullDataApps = RequestAPI.getJSON(ENDPOINTAPPS);
            
            return fullDataApps.getJSONObject("applist").getJSONArray("apps");
            
        } catch (IOException ex) {
            System.err.println("Erro (SteamGames.getAllAppsId): " +ex);
        }
        
        return null;
    }

    private static String getConcateGenres(JSONArray fullGenres) {
        if(fullGenres != null){
            ArrayList<String> genres = new ArrayList<String>();
            String contenedGenres = "";

            fullGenres.forEach(g -> {
                JSONObject genreJSON = (JSONObject) g;
                String genre = ValidadeJSON.getJSONString(genreJSON, "description");
                
                genres.add(genre);
            });
            
            for(String genre : genres)
                contenedGenres += String.format("%s - ", genre);
            
            contenedGenres = contenedGenres.substring(0, contenedGenres.length() - 3);
            
        return contenedGenres;
        }
        
        else
            return null;
    }

    private static Boolean isMultiplayer(JSONArray fullCategories) {
        return fullCategories != null? 
                getCategoriesId(fullCategories).contains(1) : false;
    }
    
    private static Boolean isSinglePlayer(JSONArray fullCategories) {
        return fullCategories != null?
                getCategoriesId(fullCategories).contains(2) : false;                            
    }
    
    private static ArrayList<Integer> getCategoriesId(JSONArray fullCategories){
            ArrayList<Integer> categoriesId = new ArrayList<Integer>();
            
            fullCategories.forEach(c ->{
                JSONObject categoryJSON = (JSONObject) c;
                Integer categoryId = ValidadeJSON.getJSONInt(categoryJSON, "id");
                
                categoriesId.add(categoryId);
            });
            
            return categoriesId;
        }
    
}
