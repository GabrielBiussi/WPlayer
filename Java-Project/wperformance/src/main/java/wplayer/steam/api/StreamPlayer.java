package wplayer.steam.api;
        
import wplayer.json.ValidadeJSON;
import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class StreamPlayer {

private static final String ENDPOINTPLAYER = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+AccountAPISteam.key+"&steamids="; 
private static final String ENDPOINTFRIENDS = " http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key="+AccountAPISteam.key+"&relationship=friend&steamid=";
private static final String FIELD = "PLAYER_ID";
private static final String TABLE = "PLAYER"; 

String steamid = "76561198166467789";
        
        String http = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+AccountAPISteam.key+"&steamids="+steamid;

        JSONObject refinedFullPlayerData = RequestAPI.getJSON(http).getJSONObject("response");
       
        JSONArray arrayPlayerData = refinedFullPlayerData.getJSONArray("players");
        
        JSONObject playerData = arrayPlayerData.getJSONObject(0);


String nickname = ValidadeJSON.getJSONString(playerData, "personaname");
String profileUrl = ValidadeJSON.getJSONString(playerData, "profileurl");
String avatar = ValidadeJSON.getJSONString(playerData, "avatar");
String personaState = getPersonaState(ValidadeJSON.getJSONInt(playerData, "personastate"));
String visibilityPermission = ValidadeJSON.getJSONInt(playerData, "communityvisibilitystate");
Integer profileState = ValidadeJSON.getJSONInt(playerData, "profilestate");
String lastLogoff = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "lastlogoff"));
Integer commentPermission = ValidadeJSON.getJSONInt(playerData, "commentpermission");
String name = ValidadeJSON.getJSONInt(playerData, "name");
String creationData = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "timecreated"));
String currentGameId = ValidadeJSON.getJSONString((playerData, "gameid"));
    

System.out.println("NickName: " +nickname + "\n" +
                           "ProfileUrl: " + profileurl + "\n" +
                           "Avatar: " + avatar + "\n" +
                           "PersonaState: " + personastate + "\n" +
                           "VisibilityPermission: " + communityvisibilitystate + "\n" +
                           "ProfileState: " + profileState + "\n" +
                           "LastLogoff: " + lastlogoff + "\n" +
                           "CommentPermission: " + commentpermission + "\n" +
                           "Name: " + name + "\n" +
                           "CreationData: " + timecreated + "\n" +
                           "CurrentGameId: " + gameid + "\n" +
                           

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
}
