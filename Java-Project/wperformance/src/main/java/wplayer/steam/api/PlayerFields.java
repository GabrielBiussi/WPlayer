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
public class PlayerFields {
    private String id;
    private String nickname;
    private String profileURL;
    private String avatar;
    private String personaState;
    private String visibilityPermission;
    private String lastLogoff;
    private String name;
    private String creationDate;
    private String currentGameID;
    private Boolean isInsert;

    public PlayerFields(String id, String nickname, String profileURL, String avatar, String personaState, String visibilityPermission, String lastLogoff, String name, String creationDate, String currentGameID) {
        this.id = id;
        this.nickname = nickname;
        this.profileURL = profileURL;
        this.avatar = avatar;
        this.personaState = personaState;
        this.visibilityPermission = visibilityPermission;
        this.lastLogoff = lastLogoff;
        this.name = name;
        this.creationDate = creationDate;
        this.currentGameID = currentGameID;
    }

    public String getId() {
        return this.id;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getProfileURL() {
        return this.profileURL;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getPersonaState() {
        return this.personaState;
    }

    public String getVisibilityPermission() {
        return this.visibilityPermission;
    }

    public String getLastLogoff() {
        return this.lastLogoff;
    }

    public String getName() {
        return this.name;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public String getCurrentGameID() {
        return this.currentGameID;
    }
    
    public Boolean isInsert(){
        return this.isInsert;
    }
    
    public void setInsert(Boolean isInsert){
        this.isInsert = isInsert;
    }
     
}
