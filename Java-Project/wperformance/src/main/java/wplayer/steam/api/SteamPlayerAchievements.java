/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.json.ValidadeJSON;
import wplayer.logtxt.CriarLog;

/**
 *
 * @author petter
 */
public class SteamPlayerAchievements {
    private static final String TABLE = "PLAYER_ACHIEVEMENTS";
    private static final String INSERTSTATEMENT = String.format(
                                                    "INSERT INTO %s "
                                                  + "(PLAYER_ID, GAME_ID, ACHIEVEMENT_KEY, UNLOCK_TIME) "
                                                  + "VALUES (?, ?, ?, ?)", TABLE);
    private static final String QUERYSTATEMENT = String.format(
                                                     "SELECT "
                                                      + "ACHIEVEMENT_KEY "
                                                     + "FROM %s "
                                                    + "WHERE PLAYER_ID = ? "
                                                      + "AND GAME_ID = ?", TABLE);
    
    
    public static String getEndPoint(String steamId, String gameId){
        return String.format("http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?appid=%s&key=%s&steamid=%s",
                            gameId, AccountAPISteam.key, steamId);
    }
    
    public static JSONObject getPlayerStats(String endPoint){
        try {
            
            JSONObject pageFull = RequestAPI.getJSON(endPoint);
            
            return ValidadeJSON.getJSONObject(pageFull, "playerstats");
            
        } catch (IOException ex) {
            CriarLog.WriteLog("Erro! "+ex);
            System.err.println("Erro: "+ex);
        }
        
        return null;
    }
    
    public static ArrayList<PlayerAchievementsFields> getAchievementsFields(JSONArray achievementsList, String steamId, String gameId){
      ArrayList<PlayerAchievementsFields> achievementsFields = new ArrayList<PlayerAchievementsFields>();  
      
      if(achievementsList != null){
        achievementsList.forEach(a -> {
            JSONObject achievement = (JSONObject) a;

            String achievementKey = ValidadeJSON.getJSONString(achievement, "apiname");
            Boolean achieved = ValidadeJSON.getJSONInt(achievement, "achieved") == 1;
            String unlockTime = ConverterUnixTime.converterUnixToString(
                                        ValidadeJSON.getJSONInt(achievement, "unlocktime"));

            achievementsFields.add(new PlayerAchievementsFields(
                                   steamId, achievementKey, achieved, unlockTime));

        });
      }
      
      return achievementsFields;
    }
 
    public static JSONArray getAchievementsList(JSONObject playerStats){
        return ValidadeJSON.getJSONArray(playerStats, "achievements");
    }
    
    public static void insertAchievementsData(String steamId, String gameId){
        String endPoint = getEndPoint(steamId, gameId);
        JSONObject playerStats = getPlayerStats(endPoint);
        JSONArray achievementsList = getAchievementsList(playerStats);
        ArrayList<String> achievementsInDatabase = getAchievementsInDatabase(steamId, gameId);
        ArrayList<PlayerAchievementsFields> achievementsFields = getAchievementsFields(achievementsList,
                                                                                        steamId, gameId);
        Connection connection = null;
        PreparedStatement statement = null;
        
        try{
            
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(INSERTSTATEMENT);
            
            for(PlayerAchievementsFields achievement : achievementsFields){
                if (achievement.isAchieved() && !achievementsInDatabase.contains(achievement.getAchievementKey())) {
             
                    statement.setString(1, achievement.getSteamId());
                    statement.setString(2, gameId);
                    statement.setString(3, achievement.getAchievementKey());
                    statement.setString(4, achievement.getUnlockTime());
                    
                    statement.addBatch();
                }
            }
            
            statement.executeBatch();
            
        }catch(SQLException ex){
            CriarLog.WriteLog("Erro! Falha em  statement.executeBatch()  "+ex);
            System.err.println("Erro: "+ex);
        }finally{
            DBConnection.closeConnection(connection, statement);
        }
        
    }
    
    public static ArrayList<String> getAchievementsInDatabase(String steamId, String gameId){
        ArrayList<String> achievementsInDatabase = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try{
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(QUERYSTATEMENT);
            
            statement.setString(1, steamId);
            statement.setString(2, gameId);
            
            statement.addBatch();
            resultSet = statement.executeQuery();
            
            while(resultSet.next())
                achievementsInDatabase.add(resultSet.getString("ACHIEVEMENT_KEY"));
            
            return achievementsInDatabase;
            
        }catch(SQLException ex){
            CriarLog.WriteLog("Erro! Falha ao retornar as conquistas do Banvo de Dados "+ex);
            System.err.println("Erro: "+ex);
        }finally{
            DBConnection.closeConnection(connection, statement, resultSet);
        }
        
        return achievementsInDatabase;
    }
    
}

 