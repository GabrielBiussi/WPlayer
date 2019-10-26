package wplayer.steam.api;
        
import wplayer.json.ValidadeJSON;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.database.DBQuery;

public class SteamPlayer {

private static final String ENDPOINTPLAYER = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+AccountAPISteam.key+"&steamids="; 
private static final String ENDPOINTFRIENDS = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key="+AccountAPISteam.key+"&relationship=friend&steamid=";
private static final String FIELD = "PLAYER_ID";
private static final String TABLE = "PLAYER";
private static final String INSERTSQL = String.format(
                                               "INSERT INTO %s "
                                             + "(PLAYER_ID, PLAYER_NICKNAME, PLAYER_PROFILE_URL, PLAYER_AVATAR, "
                                             + "PLAYER_PERSONA_STATE, PLAYER_VISIBILITY_PERMISSION, PLAYER_LAST_LOGOFF, "
                                             + "PLAYER_NAME, PLAYER_CREATION_DATA, PLAYER_CURRENT_GAME_ID) "
                                             + "VALUES (?,?,?,?,?,?,?,?,?,?)",
                                                TABLE);
private static final String UPDATESQL = String.format(
                                                "UPDATE %s SET PLAYER_NICKNAME = ?,"
                                                            + "PLAYER_PROFILE_URL = ?,"
                                                            + "PLAYER_AVATAR = ?,"
                                                            + "PLAYER_PERSONA_STATE = ?,"
                                                            + "PLAYER_VISIBILITY_PERMISSION = ?,"
                                                            + "PLAYER_LAST_LOGOFF = ?,"
                                                            + "PLAYER_NAME = ?,"
                                                            + "PLAYER_CREATION_DATA = ?,"
                                                            + "PLAYER_CURRENT_GAME_ID = ? "
                                                        + "WHERE PLAYER_ID = ?", TABLE);



public static void updatePlayers(String steamId){
    Connection connection = null;
    PreparedStatement statementInsert = null;
    PreparedStatement statementUpdate = null;
    ArrayList<PlayerFields> players = getPlayersWithBool(getPlayersData(steamId));
    PlayerFields p = null;
    try {
        connection = DBConnection.getConnection();
        statementInsert = connection.prepareStatement(INSERTSQL);
        statementUpdate = connection.prepareStatement(UPDATESQL);
        
        for(PlayerFields player : players){
            p = player;
           if(player.isInsert()){
              statementInsert.setString(1, player.getId());
              statementInsert.setString(2, player.getNickname());
              statementInsert.setString(3, player.getProfileURL());
              statementInsert.setString(4, player.getAvatar());
              statementInsert.setString(5, player.getPersonaState());
              statementInsert.setString(6, player.getVisibilityPermission());
              statementInsert.setString(7, player.getLastLogoff());
              statementInsert.setString(8, player.getName());
              statementInsert.setString(9, player.getCreationDate());
              statementInsert.setString(10, player.getCurrentGameID());
              
              statementInsert.addBatch();
              
               System.out.println("foi: "+player.getId()+ " "+player.getName());
           }
           else{
              statementUpdate.setString(1, player.getNickname());
              statementUpdate.setString(2, player.getProfileURL());
              statementUpdate.setString(3, player.getAvatar());
              statementUpdate.setString(4, player.getPersonaState());
              statementUpdate.setString(5, player.getVisibilityPermission());
              statementUpdate.setString(6, player.getLastLogoff());
              statementUpdate.setString(7, player.getName());
              statementUpdate.setString(8, player.getCreationDate());
              statementUpdate.setString(9, player.getCurrentGameID());
              statementUpdate.setString(10, player.getId());
              
              statementUpdate.addBatch();

              System.out.println("foi: "+player.getId()+ " "+player.getName());
           }
          
        }
        
        statementInsert.executeBatch();
        statementUpdate.executeBatch();
       
    } catch (SQLException ex) {
        System.err.println("Erro ao Insert/Update Player " +ex + " "+ p.getId()+ " "+ p.getName());
    } finally{
        try {
            statementUpdate.close();
            statementInsert.close();
        } catch (SQLException ex) {
            System.err.println("Erro ao fechar UPDATE: "+ex);
        }
        DBConnection.closeConnection(connection);
    }
}

private static ArrayList<PlayerFields> getPlayersWithBool(ArrayList<PlayerFields> players){
    ArrayList<PlayerFields> playersWithBool = players;
    ArrayList<String> playerIds = new ArrayList<String>();
    
    for(PlayerFields player : players){
        playerIds.add(player.getId());
    }
    
    Map<String, Boolean> dictionaryPlayers = DBQuery.hasRowsInTable(playerIds, FIELD, TABLE);
    
    dictionaryPlayers.keySet().forEach((steamId) -> {
        if(dictionaryPlayers.get(steamId)){
           playersWithBool.forEach(p -> {
           if(p.getId().equals(steamId))
               p.setInsert(false);
          });
        }
        else{
          playersWithBool.forEach(p -> {
           if(p.getId().equals(steamId))
               p.setInsert(true);
          });
        }
           
    });
    
    return playersWithBool;
}

private static ArrayList<PlayerFields> getPlayersData(String steamId){
    String endPointPlayers = getEndPointPlayers(getPlayerFriends(steamId));
    ArrayList<PlayerFields> playersFields = new ArrayList<PlayerFields>();
    
    try {
        JSONObject fullPlayersData = RequestAPI.getJSON(endPointPlayers);
        
        JSONObject reponse = ValidadeJSON.getJSONObject(fullPlayersData, "response");
        
        JSONArray playersData = ValidadeJSON.getJSONArray(reponse, "players");
        
        playersData.forEach(p -> {
            JSONObject playerData = (JSONObject) p;
            
            String id = ValidadeJSON.getJSONString(playerData, "steamid");
            String nickname = ValidadeJSON.getJSONString(playerData, "personaname");
            String profileURL = ValidadeJSON.getJSONString(playerData, "profileurl");
            String avatar = ValidadeJSON.getJSONString(playerData, "avatarfull");
            String personaState = getPersonaState(ValidadeJSON.getJSONInt(playerData, "personastate"));
            String visibilityPermission = getVisibilityPermission(ValidadeJSON.getJSONInt(playerData, "communityvisibilitystate"));
            String lastLogoff = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "lastlogoff"));
            String namePlayer = ValidadeJSON.getJSONString(playerData, "realname");
            String timeCreated = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "timecreated")); //"18/03/2019 23:59:59";
            String currentGameId = ValidadeJSON.getJSONString(playerData, "gameid");
            
            playersFields.add(new PlayerFields(id, nickname, profileURL, avatar, personaState,
                    visibilityPermission, lastLogoff, namePlayer, timeCreated, currentGameId));
        });
        
        return playersFields;
        
    } catch (IOException ex) {
        System.err.println("Erro na aquisição de todos os Players: "+ex);
    }
    
    return null;
}

private static String getEndPointPlayers(ArrayList<String> playersIds){
    String endPointFull = ENDPOINTPLAYER;
    
    for(String playerId : playersIds)
        endPointFull += String.format("%s,", playerId);
    
    endPointFull = endPointFull.substring(0, endPointFull.length() - 1);
    
    System.out.println(endPointFull);
    return endPointFull;
}


private static ArrayList<String> getPlayerFriends(String steamId){
    ArrayList<String> friendsIds = new ArrayList<String>();
    
    try {
        JSONObject pageFriends = RequestAPI.getJSON(ENDPOINTFRIENDS+steamId);
        
        JSONObject friendsList = ValidadeJSON.getJSONObject(pageFriends, "friendslist");
        
        JSONArray friends = ValidadeJSON.getJSONArray(friendsList, "friends");
        
        friends.forEach(f -> {
            JSONObject friend = (JSONObject) f;
            
            friendsIds.add(ValidadeJSON.getJSONString(friend, "steamid"));
        });
        
        friendsIds.add(steamId);
        
        return friendsIds;
        
    } catch (IOException ex) {
        System.err.println("Erro na aquisição dos amigos do steamId: " 
                            +steamId+ ": "+ex);
    }
    
    return null;
}
                           

   private static String getPersonaState(int personastate){
          switch(personastate){
              case 0: return "Offline";
              case 1: return "Online";
              case 2: return "Busy";
              case 3: return "Away";
              case 4: return "Snooze";
              case 5: return "Looking to trade";
              case 6: return "Looking to play";
              default: return null;
          }     
        }
   
   private static String getVisibilityPermission(Integer visibilityPermission){
       return visibilityPermission == 3? "Público" : "Privado";
   }
}
