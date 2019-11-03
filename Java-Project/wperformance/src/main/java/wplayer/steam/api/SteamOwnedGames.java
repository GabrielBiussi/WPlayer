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
 * @author Gabriel, Petter, Giulianno
 */
public class SteamOwnedGames {
    private static final String ENDPOINT = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=3831A87503D7D84D68550EE188077B1D&include_appinfo=true&format=json&include_played_free_games=true&steamid=";
    private static final String FIELD = "PLAYER_ID";
    private static final String TABLE = "OWNED_APPS";
    private static final String INSERTSTATEMENT = String.format(
                                                  "INSERT INTO %s "
                                                + "(PLAYER_ID, GAME_ID, GAME_COMMUNITY_PERMISSION, "
                                                + "PLAYTIME_FULL, PLAYTIME_2WEEKS) "
                                                + "VALUES (?, ?, ?, ?, ?)", TABLE);
    private static final String UPDATESTATEMENT = String.format(
                                                  "UPDATE %s SET "
                                                + "PLAYER_ID = ?, GAME_ID = ?,"
                                                + "GAME_COMMUNITY_PERMISSION = ?,"
                                                + "PLAYTIME_FULL = ?,"
                                                + "PLAYTIME_2WEEKS = ? "
                                                + "WHERE PLAYER_ID = ? "
                                                  + "AND GAME_ID = ?", TABLE);
    private static final String SELECTQUERY = String.format("SELECT "
                                                             + "GAME_ID "
                                                            + "FROM %s "
                                                           + "WHERE PLAYER_ID = ?", TABLE);
    
    public static void updateOwnedGames(String steamid){
        ArrayList<GameFields> games = hasRowsInTable(steamid, getGamesArray(steamid));
        
        if(games != null){
        
        Connection connection = DBConnection.getConnection();
        PreparedStatement statementInsert = null;
        PreparedStatement statementUpdate = null;
        
        try{
           statementInsert = connection.prepareStatement(INSERTSTATEMENT);
           statementUpdate = connection.prepareStatement(UPDATESTATEMENT);

            for (GameFields game : games) {
                if(!game.isInsert){
                        statementUpdate.setString(1, game.getPlayerId());
                        statementUpdate.setString(2, game.getGameId());
                        statementUpdate.setInt(3, game.getGameCommunityPermission());
                        statementUpdate.setDouble(4, game.getPlayTimeFull());
                        statementUpdate.setDouble(5, game.getPlayTime2Weeks());
                        statementUpdate.setString(6, game.getPlayerId());
                        statementUpdate.setString(7, game.getGameId());
                        

                        statementUpdate.addBatch();
                }
                else{
                        statementInsert.setString(1, game.getPlayerId());
                        statementInsert.setString(2, game.getGameId());
                        statementInsert.setInt(3, game.getGameCommunityPermission());
                        statementInsert.setDouble(4, game.getPlayTimeFull());
                        statementInsert.setDouble(5, game.getPlayTime2Weeks());

                        statementInsert.addBatch();
                 }        
            }
            
            statementInsert.executeBatch();
            statementUpdate.executeBatch();
            
        } catch (SQLException ex) {
            System.err.println("Erro nos Inserts/Updates: "+ex);
            System.err.println("Erro nos Inserts/Updates: "+ex);
        } finally{
            try {
                statementInsert.close();
            } catch (SQLException ex) {
                System.err.println("Erro ao fechar Statement: "+ex);
            }
            DBConnection.closeConnection(connection, statementUpdate);
        }
    }
}
    
    private static ArrayList<GameFields> hasRowsInTable(String playerId, ArrayList<GameFields> gamesList){
        if(gamesList == null)
            return null;
        
        ArrayList<GameFields> games = gamesList;
        ArrayList<String> containGames = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DBConnection.getConnection();
            prepareStatement = connection.prepareStatement(SELECTQUERY);
            
            prepareStatement.setString(1, playerId);
            
            prepareStatement.addBatch();
            
            resultSet = prepareStatement.executeQuery();
            
            while(resultSet.next())
                containGames.add(resultSet.getString("GAME_ID"));
            
            for (GameFields game : games)
                game.setInsert(!containGames.contains(game.getGameId()));
                
            return games;
            
        } catch (SQLException ex) {
            System.err.println("Erro na conexão com banco de dados: "+ex);
        } finally{
            DBConnection.closeConnection(connection, prepareStatement, resultSet);
        }
        
        return null;
    }
    
    private static ArrayList<GameFields> getGamesArray(String steamid){ 
        ArrayList<GameFields> gamesArray = new ArrayList<GameFields>();
        JSONObject pageFull = getPageFull(steamid);
        JSONArray games = ValidadeJSON.getJSONArray(ValidadeJSON.getJSONObject(pageFull, "response"), "games");
        
        if(games == null)
            return null;
        
        
        for (Object g : games){
           JSONObject game = (JSONObject) g;
           gamesArray.add(new GameFields(steamid,
                                         ValidadeJSON.getJSONInt(game, "appid").toString(),
                                         getCommunityVisible(ValidadeJSON.getJSONBoolean(game, "has_community_visible_stats")),
                                         getTimeInHours(ValidadeJSON.getJSONInt(game, "playtime_forever")),
                                         getTimeInHours(ValidadeJSON.getJSONInt(game, "playtime_2weeks"))));
        }
        
        return gamesArray;
    }
    
    private static JSONObject getPageFull(String steamid){
        try {
            JSONObject pageFull = RequestAPI.getJSON(ENDPOINT+steamid);
            System.out.println(ENDPOINT+steamid);
            return pageFull;
        } catch (IOException ex) {
            System.err.println("Erro na aquisição da API (SteamOwnedGames): "+ex);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex1) {
                System.err.println("Se não der, não deu");
            }
        }
        return null;
    }
   
    private static Double getTimeInHours(Integer time){
        return time != null?  Double.valueOf(time)/60 : 0;
    }
    
    private static Integer getCommunityVisible(Boolean isVisible){
        return isVisible != null? isVisible? 1 : 0 : 0;
    }
    
}
