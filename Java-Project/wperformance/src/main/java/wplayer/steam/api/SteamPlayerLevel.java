/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.json.ValidadeJSON;

/**
 *
 * @author petter
 */
public class SteamPlayerLevel {
    private static final String ENDPOINT = "https://api.steampowered.com/IPlayerService/GetBadges/v1/?key="+AccountAPISteam.key+"&steamid=";
    private static final String TABLE = "PLAYER";
    private static final String FIELD = "PLAYER_ID";
    private static final String UPDATESTATEMENT = String.format(
                                                  "UPDATE %s "
                                                + "SET PLAYER_LEVEL = ?,"
                                                    + "PLAYER_XP = ?,"
                                                    + "PLAYER_XP_TO_NEXT_LEVEL = ? "
                                                + "WHERE %s = ?", TABLE, FIELD);
                                                
    
    public static Integer getLevel(JSONObject fullPage){
            Integer level = ValidadeJSON.getJSONInt(
                            ValidadeJSON.getJSONObject(fullPage, "response"), "player_level");
            
            return level == null? 0 : level;
    }
    
    public static JSONObject getPageFull(String steamId){
        try {
            return RequestAPI.getJSON(ENDPOINT+steamId);
        } catch (IOException ex) {
            System.err.println("Erro: "+ex);
        }
        
        return null;
    }
    
    public static Integer getExp(JSONObject pageFull){
        Integer exp = ValidadeJSON.getJSONInt(
                      ValidadeJSON.getJSONObject(pageFull, "response"), "player_xp");
        
        return exp == null? 0 : exp;
    }
    
    public static Integer getExtRemaining(JSONObject pageFull){
        Integer exp = ValidadeJSON.getJSONInt(
                ValidadeJSON.getJSONObject(pageFull, "response"), "player_xp_needed_to_level_up");
    
        return exp == null? 0: exp;
    }
    
    public static void updatePlayer(String steamId){
        Connection connection = null;
        PreparedStatement statement = null;
        JSONObject pageFull = getPageFull(steamId);
     
        try{
            connection = DBConnection.getConnection();
            statement = connection.prepareStatement(UPDATESTATEMENT);
            
            statement.setInt(1, getLevel(pageFull));
            statement.setInt(2, getExp(pageFull));
            statement.setInt(3, getExtRemaining(pageFull));
            statement.setString(4, steamId);
            
            statement.addBatch();
            statement.execute();
            
        }catch(SQLException ex){
            System.err.println("Erro:" +ex);
        }finally{
            DBConnection.closeConnection(connection, statement);
        }
    }
    
}
