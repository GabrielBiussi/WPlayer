/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.steam.api;

import java.util.ArrayList;

/**
 *
 * @author petter
 */
public class GamePercentageFields {
    String gameId;
    ArrayList<Double> percentagesList;
    Boolean isInsert;

    public GamePercentageFields(String gameId, ArrayList<Double> percentagesList, Boolean isInsert) {
        this.gameId = gameId;
        this.percentagesList = percentagesList;
        this.isInsert = isInsert;
    }

    public String getGameId() {
        return this.gameId;
    }

    public ArrayList<Double> getPercentagesList() {
        return this.percentagesList;
    }

    public Boolean isInsert() {
        return this.isInsert;
    }
    
    public void setInsert(Boolean isInsert){
        this.isInsert = isInsert;
    }
    
}
