
import wplayer.steam.api.SteamAchievements;
import wplayer.steam.api.SteamGames;
import wplayer.steam.api.SteamOwnedGames;
import wplayer.steam.api.SteamPlayer;
import wplayer.steam.api.SteamPlayerAchievements;
import wplayer.steam.api.SteamPlayerLevel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author petter
 */
public class Test {
    public static void main(String[] args) {
        String steamid = "76561198320279811";
        // String gameId = "291550";
        String gameId = "240";
        
        //SteamAchievements.updateAchievements(gameId);
        //SteamOwnedGames.updateOwnedGames(steamid);
        SteamGames.updateAppsTable();
        //SteamPlayerAchievements.insertAchievementsData(steamid, gameId);
    }
}
