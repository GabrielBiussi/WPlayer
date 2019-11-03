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

/**
 *
 * @author petter
 */
public class SteamAchievements {
    private static final String ENDPOINT = "http://api.steampowered.com/ISteamUserStats/GetSchemaForGame/v2/?key="+AccountAPISteam.key+"&appid=";
    private static final String TABLE = "ACHIEVEMENTS";
    private static final String INSERTSTATEMENT = String.format(
                                                  "INSERT INTO %s ("
                                                + "GAME_ID, ACHIEVEMENT_KEY, ACHIEVEMENT_NAME, "
                                                + "ACHIEVEMENT_DESCRIPTION, ACHIEVEMENT_ICON, "
                                                + "ACHIEVEMENT_ICON_COMPLETED"
                                                + ") VALUES (?,?,?,?,?,?)", TABLE);
    private static final String UPDATESTATEMENT = String.format(
                                                  "UPDATE %s SET "
                                                + "ACHIEVEMENT_NAME = ?, ACHIEVEMENT_DESCRIPTION = ?, "
                                                + "ACHIEVEMENT_ICON = ?, ACHIEVEMENT_ICON_COMPLETED = ? "
                                                + "WHERE GAME_ID = ? "
                                                + "  AND ACHIEVEMENT_KEY = ?", TABLE);
    private static final String QUERYGAMES = String.format(
                                                  "SELECT "
                                                    + "DISTINCT(GAME_ID) GAME_ID "
                                                  + "FROM %s", TABLE);
    
    
    public static JSONArray getAchievementsJSONArray(String gameId){
        try {
            JSONObject pageFull = RequestAPI.getJSON(ENDPOINT+gameId);
            JSONArray achievements = ValidadeJSON.getJSONArray(
                    ValidadeJSON.getJSONObject(
                    ValidadeJSON.getJSONObject(pageFull, "game"), "availableGameStats"), 
                                                                  "achievements");
            
            // System.out.println("Tem página: " + achievements +" - " + pageFull);
            
            return achievements;
            
        } catch (IOException ex) {
            System.err.println("Erro: "+ex);
        }
        
        return null;
    }
    
    public static ArrayList<AchievementsFields> getAchievementsArray(String gameId, JSONArray achievementsPage, ArrayList<String> gamesIds){
        ArrayList<AchievementsFields> achievements = new ArrayList<AchievementsFields>();
        
        if(achievementsPage != null){
            
            achievementsPage.forEach(a -> {
                JSONObject achievement = (JSONObject) a;
                
                String achievementKey = ValidadeJSON.getJSONString(achievement, "name");
                String achievementName = ValidadeJSON.getJSONString(achievement, "displayName");
                String achievementDescription = ValidadeJSON.getJSONString(achievement, "description");
                String achievementIcon = ValidadeJSON.getJSONString(achievement, "icongray");
                String achievementIconCompleted = ValidadeJSON.getJSONString(achievement, "icon");

                achievements.add(new AchievementsFields(
                                     gameId, achievementKey, achievementName,
                                     achievementDescription, achievementIcon, 
                                     achievementIconCompleted, !gamesIds.contains(gameId)));
        
            });
            
        }
        
        return achievements;
    }
    
    public static ArrayList<String> getGamesIds(){
        ArrayList<String> gamesIds = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try{
            
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(QUERYGAMES);
            statement.addBatch();
            
            resultSet = statement.executeQuery();
            
            while(resultSet.next())
                gamesIds.add(resultSet.getString("GAME_ID"));
            
            // System.out.println("Tem gamesIds (no caso não tem)" +gamesIds);
            
            return gamesIds;
            
        }catch(SQLException ex){
            System.err.println("Erro: "+ex);
        }finally{
            DBConnection.closeConnection(connection, statement, resultSet);
        }
        
        return gamesIds;
    }
    
    
    public static void updateAchievements(String gameId){
        ArrayList<AchievementsFields> achievements = getAchievementsArray(
                                                       gameId,
                                                       getAchievementsJSONArray(gameId),
                                                       getGamesIds());
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        
        try{
            
            connection = DBConnection.getConnection();
            insertStatement = connection.prepareStatement(INSERTSTATEMENT);
            updateStatement = connection.prepareStatement(UPDATESTATEMENT);
            Boolean hasInsert = false;
            Boolean hasUpdate = false;
            
            for (AchievementsFields achievement : achievements){
            
                if(achievement.isInsert()){
                   
                    insertStatement.setString(1, achievement.getGameId());
                    insertStatement.setString(2, achievement.getAchievementKey());
                    insertStatement.setString(3, achievement.getAchievementName());
                    insertStatement.setString(4, achievement.getAchievementDescription());
                    insertStatement.setString(5, achievement.getAchievementIcon());
                    insertStatement.setString(6, achievement.getAchievementIconCompleted());
                    
                    insertStatement.addBatch();
                    
                    hasInsert = true;
                }
                else{
                    
                    updateStatement.setString(1, achievement.getAchievementName());
                    updateStatement.setString(2, achievement.getAchievementDescription());
                    updateStatement.setString(3, achievement.getAchievementIcon());
                    updateStatement.setString(4, achievement.getAchievementIconCompleted());
                    updateStatement.setString(5, achievement.getGameId());
                    updateStatement.setString(6, achievement.getAchievementKey());
                    
                    updateStatement.addBatch();
                    
                    hasUpdate = true;
                }
                
                if(hasInsert){
                    insertStatement.executeBatch();
                }
                
                if(hasUpdate){
                    updateStatement.executeBatch();
                }
            }
            
        }catch(SQLException ex){
            System.err.println("Erro (MAIS PROVAVEL): "+ex);
        }finally{
            try{
                if(insertStatement != null)
                    insertStatement.close();
            
            }catch(SQLException ex1){
                System.err.println("Erro: "+ex1);
            }
            DBConnection.closeConnection(connection, updateStatement);
        }
        
    }

}