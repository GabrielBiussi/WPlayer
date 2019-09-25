package wplayer.steam.api;


public class StreamPlayer {
private String Steamid="";
private static final String ENDPOINTPLAYER = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+AccountAPISteam.key+"&steamids="; 
private static final String ENDPOINTFRIENDS = " http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key="+AccountAPISteam.key+"&relationship=friend&steamid=";
private static final String FIELD = "PLAYER_ID";
private static final String TABLE = "PLAYER"; 
}
