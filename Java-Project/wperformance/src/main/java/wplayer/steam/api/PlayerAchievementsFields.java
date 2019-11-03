/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

/**
 *
 * @author petter
 */
public class PlayerAchievementsFields {
    private String steamId;
    private String achievementKey;
    private Boolean achieved;
    private String unlockTime;

    public PlayerAchievementsFields(String steamId, String achievementKey, Boolean achieved, String unlockTime) {
        this.steamId = steamId;
        this.achievementKey = achievementKey;
        this.achieved = achieved;
        this.unlockTime = unlockTime;
    }

    public String getSteamId() {
        return steamId;
    }

    public String getAchievementKey() {
        return achievementKey;
    }

    public Boolean isAchieved() {
        return achieved;
    }

    public String getUnlockTime() {
        return unlockTime;
    }
    
}
