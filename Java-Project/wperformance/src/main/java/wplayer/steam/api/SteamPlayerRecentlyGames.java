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
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.database.DBQuery;
import wplayer.json.ValidadeJSON;

/**
 *
 * @author Aluno
 */
public class SteamPlayerRecentlyGames {

    private static final String ENDPOINT = "http://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v0001/?key=" + AccountAPISteam.key + "&steamid=";
    private static final String FIELD = "PLAYER_ID";
    private static final String TABLE = "PLAYER_RECENTLY_GAMES";
    private static final String SQLINSERT = "INSERT INTO PLAYER_RECENTLY_GAMES VALUES (?, ?)";
    private static final String SQLUPDATE = "UPDATE PLAYER_RECENTLY_GAMES SET GAME_ID = ? WHERE PLAYER_ID = ?";

//void é basicamente não devolver nada
    public static void updateRecentlyGames(String steamid) { 
        Connection connection = null;
        //try-catch serve para tratar um suposto erro
        try {
            //Criaamos uma variável para puxar o jason que o endpoint fornece
            JSONObject dados = RequestAPI.getJSON(ENDPOINT + steamid);
            System.out.println(dados);
            JSONObject response = ValidadeJSON.getJSONObject(dados, "response");

            Integer gameCount = ValidadeJSON.getJSONInt(response, "total_count");
            
            ArrayList<String> appsIds = new ArrayList<>();

            if (gameCount != null) {
                  JSONArray games = ValidadeJSON.getJSONArray(response, "games");
                
                   games.forEach(g -> {
                       JSONObject game = (JSONObject) g;
                       appsIds.add(ValidadeJSON.getJSONInt(game, "appid").toString());
                   });
                
                System.out.println(appsIds);
            }
            
            
            connection = DBConnection.getConnection();
            
            
            
            PreparedStatement statementINSERT = connection.prepareStatement(SQLINSERT);
            PreparedStatement statementUPDATE = connection.prepareStatement(SQLUPDATE);
            
            appsIds.forEach(item -> {
                String appid = (String) item;
                
                try {
                   if(DBQuery.hasRowInTable(appid, "GAME_ID", TABLE)){
                    statementUPDATE.setString(1, appid);
                    statementUPDATE.setString(2, steamid);
                    
                    statementUPDATE.addBatch();
                    
                    statementUPDATE.executeLargeBatch();
                } 
                   else{
                    statementINSERT.setString(1, steamid);
                    statementINSERT.setString(2, appid);
                    
                    statementINSERT.addBatch(); 
                    
                    statementINSERT.executeBatch();
                }
                    
                } catch (SQLException ex) {
                    System.out.println("Erro ao INSERIR DADOS: " +ex);
                }
               
            });
           
        } catch (IOException ex) {
            System.out.println("Erro na classe RecentlyGames (updateRecentlyGames), ERRO NO REQUEST DA API");
        } catch (SQLException ex) {
           System.out.println("Erro na classe RecentlyGames (updateRecentlyGames), ERRO NA CONEXÃO");
        } finally{
            DBConnection.closeConnection(connection);
        }

    }

}
