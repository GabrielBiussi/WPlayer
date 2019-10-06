package wplayer.steam.api;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private static final String INSERTSTATEMENT = String.format(
                         "INSERT INTO %s VALUES (?,?,?,?,?,?,?,?,?,?)", TABLE);
    private static final String UPDATESTATEMENT = String.format(
                         "UPDATE %s SET APP_NAME=?,APP_TYPE=?,APP_REQUIRED_AGE=?,APP_MAIN_GAME=?,"
                       + "APP_GENRES=?,APP_IS_FREE=?,APP_IS_MULTIPLAYER=?,APP_IS_SINGLEPLAYER=?,"
                       + "APP_PRICE=? WHERE APP_ID=?", TABLE);
 
    public static void updateAppsTable(){
        Map<String, Boolean> dictionaryApps = getAppsDictionary();
        ArrayList<String> ignored = getListIgnoredApps();
        
        dictionaryApps.keySet().forEach((appID) -> {
            if(dictionaryApps.get(appID))
                System.out.println("f");//  updateAppDetails(appID, false);
            else
                if(!ignored.contains(appID))
                    updateAppDetails(appID, true);
        });
    }
    
    private static void updateAppDetails(String id, Boolean isInsert){
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            JSONObject fullDetails = RequestAPI.getJSON(ENDPOINTDETAILS+id);
            
            JSONObject refinedDetails = fullDetails.getJSONObject(id);
            
            if(ValidadeJSON.getJSONBoolean(refinedDetails, "success")){
                
                
                JSONObject data = ValidadeJSON.getJSONObject(refinedDetails, "data");
                JSONObject fullGame = ValidadeJSON.getJSONObject(data, "fullgame");
                JSONObject priceOverview = ValidadeJSON.getJSONObject(data, "price_overview");
                        
                JSONArray fullCategories = ValidadeJSON.getJSONArray(data, "categories");
                JSONArray fullGenres = ValidadeJSON.getJSONArray(data, "genres");
                
                String appId = id;
                String nameApp = ValidadeJSON.getJSONString(data, "name");
                String type = ValidadeJSON.getJSONString(data, "type");
                String fullGameID = ValidadeJSON.getJSONString(fullGame, "appid");
                String genres = getConcateGenres(fullGenres);
                
                Integer requiredAge = ValidadeJSON.getJSONInt(data, "required_age");
                Integer isFree = ValidadeJSON.getJSONBoolean(data, "is_free")? 1 : 0;
                Integer isMultiplayer = isMultiplayer(fullCategories)? 1 : 0;
                Integer isSinglePlayer = isSinglePlayer(fullCategories)? 1 : 0;
                
                Double price = getPriceApp(ValidadeJSON.getJSONInt(priceOverview, "final"));
                
                connection = DBConnection.getConnection();
                
                if(isInsert){
                    statement = connection.prepareStatement(INSERTSTATEMENT);
                    
                    statement.setString(1, appId);
                    statement.setString(2, nameApp);
                    statement.setString(3, type);
                    statement.setInt(4, requiredAge);
                    statement.setString(5, fullGameID);
                    statement.setString(6, genres);
                    statement.setInt(7, isFree);
                    statement.setInt(8, isMultiplayer);
                    statement.setInt(9, isSinglePlayer);
                    statement.setDouble(10, price);
                    
                    statement.addBatch();
                    
                    statement.executeBatch();
                    
                    System.out.println(nameApp + ": Inserido");
                    
                    try {
                sleep(1000);
            } catch (InterruptedException ex1) {
                System.err.println("Sleep error: "+ex1);
            }
                }
                else{
                    statement = connection.prepareStatement(UPDATESTATEMENT);
                    
                    statement.setString(1, nameApp);
                    statement.setString(2, type);
                    statement.setInt(3, requiredAge);
                    statement.setString(4, fullGameID);
                    statement.setString(5, genres);
                    statement.setInt(6, isFree);
                    statement.setInt(7, isMultiplayer);
                    statement.setInt(8, isSinglePlayer);
                    statement.setDouble(9, price);
                    statement.setString(10, appId);
                    
                    statement.addBatch();
                    
                    statement.executeBatch();
                    
                    
                    System.out.println(nameApp + ": Atualizado");
                    
                    try {
                sleep(1000);
            } catch (InterruptedException ex1) {
                System.err.println("Sleep error: "+ex1);
            }
                }
                
            }
            else{
                    
                connection = DBConnection.getConnection();
                
                statement = connection.prepareStatement("INSERT INTO ID_NO_RESPONSE VALUES (?)");
                
                statement.setString(1, id);
                
                statement.execute();
                
                try {
                sleep(1000);
            } catch (InterruptedException ex1) {
                System.err.println("Sleep error: "+ex1);
            }
                //System.err.println("Erro na API p/ Aquisição"
                       //          + " de campos do appId: "+id);
            }
           
        } catch (IOException ex) {
            System.err.println("Erro (SteamGames.getFieldsDetails): " +ex);
            try {
                sleep(120000);
            } catch (InterruptedException ex1) {
                System.err.println("Sleep error: "+ex1);
            }
        } catch (SQLException ex) {
            System.err.println("Erro com INSERT/UPDATE ("+id+") (SteamGames.getFieldsDetails): "+ex);
        } finally{
            DBConnection.closeConnection(connection, statement);
        }
        
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

    private static Double getPriceApp(Integer value) {
        return value != null?
                Double.valueOf(value)/100 : 0.0;
    }

    private static ArrayList<String> getListIgnoredApps() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        ArrayList<String> listIgnored = new ArrayList<String>();
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            
            resultSet = statement.executeQuery("SELECT STEAM_ID FROM ID_NO_RESPONSE");
            
            while(resultSet.next())
                listIgnored.add(resultSet.getString("STEAM_ID"));
            
            return listIgnored;
                
        } catch (SQLException ex) {
            System.err.println("Erro lá: "+ex);
        }
        
        return null;
    }
    
}
