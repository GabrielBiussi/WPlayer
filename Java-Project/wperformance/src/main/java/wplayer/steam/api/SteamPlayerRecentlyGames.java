/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

/**
 *
 * @author Aluno
 */
public class SteamPlayerRecentlyGames {
    
private String steamid ="";
     
private static final String ENDPOINT = "http://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v0001/?key="+AccountAPISteam.key+"&steamid=";
private static final String FIELD = "PLAYER_ID";
private static final String TABLE = "PLAYER_RECENTLY_GAMES";
}
