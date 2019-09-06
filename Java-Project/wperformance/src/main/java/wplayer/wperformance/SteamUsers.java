/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.wperformance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author petter
 */
public class SteamUsers {
    public static void main(String[] args) throws MalformedURLException, IOException {
        // Constante String que grava a steamID do nosso usuário (Teste: Gui)
         //String steamid = "76561198320279811";
         String steamid = "76561198166467789";
        // String steamid = "76561198337074506";
        
        // Formação de uma String para URL (Concatenando as variáveis ao link da API da steam)
        String http = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key="+AccountAPISteam.key+"&steamids="+steamid;
        
        // Criação da variável do tipo URL (Para poder fazer conexão, mas é o mesmo link que criamos antes) 
        URL getPlayerData = new URL(http);
        
        // Criação da variável InputStreamReader, abrindo conexão com a API)
        InputStreamReader request = new InputStreamReader(getPlayerData.openStream());
        
        // Criação da variável do tipo BufferedReader, para fazer a leitura dos dados recebidos
        BufferedReader data = new BufferedReader(request);
        
        // Criação da variável String que contém os dados capturados da API.
        String fullPlayerData = data.readLine();
        
        // Printando resultados Completos da API
        // System.out.println(fullPlayerData);
       
        // Criação do Objeto JSON que recebe os dados (Para poder dividir e organizar os campos)
        JSONObject fullJSONData = new JSONObject(fullPlayerData);

        //Criando Sub Níveis para organizar o JSON -- Nível 1
        JSONObject refinedFullPlayerData  = fullJSONData.getJSONObject("response");
        
        //Criando Sub Níveis para organizar o JSON -- Nível 2 (Array);
        JSONArray arrayPlayerData = refinedFullPlayerData.getJSONArray("players");
        
        //Criando Sub Níveis para organizar o JSON -- Nível 3 (Final);
        JSONObject playerData = arrayPlayerData.getJSONObject(0);
        
        //Armazenando dados do Player:
        String nickname = ValidadeJSON.getJSONString(playerData, "personaname");
        String namePlayer = ValidadeJSON.getJSONString(playerData, "realname");
        String personaState = getPersonaState(ValidadeJSON.getJSONInt(playerData, "personastate"));
        String profileURL = ValidadeJSON.getJSONString(playerData, "profileurl");
        Integer profileState = ValidadeJSON.getJSONInt(playerData, "profilestate");
        String primaryClanID = ValidadeJSON.getJSONString(playerData, "primaryclanid");
        String avatarFull = ValidadeJSON.getJSONString(playerData, "avatarfull");
        String avatarMedium = ValidadeJSON.getJSONString(playerData, "avatarmedium");
        String avatar = ValidadeJSON.getJSONString(playerData, "avatar");
        Integer personaStateFlags = ValidadeJSON.getJSONInt(playerData, "personastateflags");
        String timeCreated = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "timecreated"));
        String lastLogoff = ConverterUnixTime.converterUnixToString(ValidadeJSON.getJSONInt(playerData, "lastlogoff"));
        String locCountryCode = ValidadeJSON.getJSONString(playerData, "loccountrycode");
        Integer communityVisibilityState = ValidadeJSON.getJSONInt(playerData, "communityvisibilitystate");
        Integer commentPermission = ValidadeJSON.getJSONInt(playerData, "commentpermission");
        
        // Printando resultados organizados:
        System.out.println("NickName: " +nickname + "\n" +
                           "Nome: " + namePlayer + "\n" +
                           "PersonaState: " + personaState + "\n" +
                           "ProfileURL: " + profileURL + "\n" +
                           "ProfileState: " + profileState + "\n" +
                           "PrimaryClanID: " + primaryClanID + "\n" +
                           "AvatarFull: " + avatarFull + "\n" +
                           "AvatarMedium: " + avatarMedium + "\n" +
                           "Avatar: " + avatar + "\n" +
                           "PersonaStateFlags: " + personaStateFlags + "\n" +
                           "TimeCreated: " + timeCreated + "\n" +
                           "CommentPermission: " + commentPermission + "\n" +
                           "LastLogoff: " + lastLogoff + "\n" +
                           "LocCountryCode: " + locCountryCode + "\n" +
                           "CommunityVisibilityState: " + communityVisibilityState + "\n");
                                              
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
       
}
