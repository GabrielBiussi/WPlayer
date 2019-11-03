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
public class GameFields {
    String playerId;
    String gameId;
    Integer gameCommunityPermission;
    Double playTimeFull;
    Double playTime2Weeks;
    Boolean isInsert;

    public GameFields(String playerId, String gameId, Integer gameCommunityPermission, Double playTimeFull, Double playTime2Weeks) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.gameCommunityPermission = gameCommunityPermission;
        this.playTimeFull = playTimeFull;
        this.playTime2Weeks = playTime2Weeks;
    }

    public Boolean isInsert(){
        return this.isInsert;
    }
    
    public void setInsert(Boolean isInsert){
        this.isInsert = isInsert;
    }
    
    public String getPlayerId() {
        return this.playerId;
    }

    public String getGameId() {
        return this.gameId;
    }

    public Integer getGameCommunityPermission() {
        return this.gameCommunityPermission;
    }

    public Double getPlayTimeFull() {
        return this.playTimeFull;
    }

    public Double getPlayTime2Weeks() {
        return this.playTime2Weeks;
    }
}
