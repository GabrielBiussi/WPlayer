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
public class SteamPlayerAchievements {  
    
    private String appPlayerId = "";
    private static final String ENDPOINT = "http://api.steampowered.com/ISteamUserStats/GetPlayerAchievements/v0001/?key="+AccountAPISteam.key+"&appid=";
    private static final String FIELD = "PLAYER_ID";
    private static final String TABLE = "PLAYER_ACHIEVEMENTS_PERCENTAGE";
}
