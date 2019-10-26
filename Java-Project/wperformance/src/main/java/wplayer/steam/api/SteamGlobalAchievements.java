/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBQuery;
import wplayer.json.ValidadeJSON;

/**
 *
 * @author petter
 */
public class SteamGlobalAchievements {
    private static final String ENDPOINT = "http://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/?format=json&gameid=";
    private static final String FIELD = "GAME_ID";
    private static final String TABLE = "GLOBAL_GAMES_ACHIEVEMENTS";
    private static final String INSERTSTATEMENT = String.format(
                                                  "INSERT INTO %s VALUES (?,?)", TABLE);
    private static final String UPDATESTATEMENT = String.format(
                                                  "UPDATE %s "
                                                   + "SET PERCENTAGE = ? "
                                                 + "WHERE %s = ?", TABLE, FIELD);
    
    public static void updateGlobalAchievements(){
        ArrayList<String> gamesIds = DBQuery.getGamesIds();
        ArrayList<GamePercentageFields> gamesFields = new ArrayList<GamePercentageFields>();
        
        for(String gameId : gamesIds){
           // gamesFields.add(new GamePercentageFields(gameId, percentagesList, Boolean.FALSE);
        }
        
    }
    
    private static ArrayList<Double> getPercentagesList(String gameId){
        ArrayList<Double> percentagesList = new ArrayList<Double>();
        
        try {
            JSONObject fullPage = RequestAPI.getJSON(ENDPOINT+gameId);
            
            JSONArray achievements = ValidadeJSON.getJSONArray(
                ValidadeJSON.getJSONObject(fullPage, "achievementpercentages"), "achievements");
            
            achievements.forEach(a -> {
                JSONObject achievement = (JSONObject) a;
                System.out.println(ValidadeJSON.getJSONArray(achievement, "percent"));
                percentagesList.add(ValidadeJSON.getJSONDouble(achievement, "percent"));
            });
            
            return percentagesList;
            
        } catch (IOException ex) {
            System.err.println("Erro: "+ex);
        }
        
        return null;
    }
    
    private static Double getPercentage(ArrayList<Double> listPercentage){ 
        Double media = 0.0;
        Double total = 0.0;
        Integer amount = listPercentage.toArray().length;
        
        for (Double percentage : listPercentage)
            total += percentage;
        
        media = (total / amount);
        
        return media;
    }

}
