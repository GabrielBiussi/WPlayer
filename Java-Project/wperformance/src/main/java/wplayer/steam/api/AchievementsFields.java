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
public class AchievementsFields {
    private String gameId;
    private String achievementKey;
    private String achievementName;
    private String achievementDescription;
    private String achievementIcon;
    private String achievementIconCompleted;
    private Boolean isInsert;

    public AchievementsFields(String gameId, String achievementKey, String achievementName, String achievementDescription, String achievementIcon, String achievementIconCompleted, Boolean isInsert) {
        this.gameId = gameId;
        this.achievementKey = achievementKey;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.achievementIcon = achievementIcon;
        this.achievementIconCompleted = achievementIconCompleted;
        this.isInsert = isInsert;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getAchievementKey() {
        return achievementKey;
    }

    public void setAchievementKey(String achievementKey) {
        this.achievementKey = achievementKey;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public String getAchievementIcon() {
        return achievementIcon;
    }

    public void setAchievementIcon(String achievementIcon) {
        this.achievementIcon = achievementIcon;
    }

    public String getAchievementIconCompleted() {
        return achievementIconCompleted;
    }

    public void setAchievementIconCompleted(String achievementIconCompleted) {
        this.achievementIconCompleted = achievementIconCompleted;
    }
    
    public Boolean isInsert(){
        return this.isInsert;
    }

    public void setInsert(Boolean isInsert) {
        this.isInsert = isInsert;
    }
    
    
}
