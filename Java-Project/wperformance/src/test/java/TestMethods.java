import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import wplayer.database.DBConnection;
import wplayer.database.DBQuery;
import wplayer.json.ValidadeJSON;
import wplayer.steam.api.RequestAPI;
import wplayer.steam.api.SteamGames;
import wplayer.steam.api.SteamOwnedGames;
import wplayer.steam.api.SteamPlayer;

/**
 *
 * @author petter
 */
public class TestMethods {
    public static void main(String[] args) {
        
          //SteamPlayer.updatePlayers("76561198120683480");
//        System.out.println("Esse Existe:" +
//                DBQuery.hasRowInTable("985530", "idApp", "GAMES"));
//        System.out.println("Esse Não:" +
//                DBQuery.hasRowInTable("1223", "idApp", "GAMES"));
//        System.out.println("Esse Não:" +
//                DBQuery.hasRowInTable("424223", "idApp", "GAMES"));
//        System.out.println("Esse Sim:" +
//                DBQuery.hasRowInTable("5555", "idApp", "GAMES"));

//        SteamPlayer.updatePlayers("76561197966717282");
            
            //SteamGames.updateAppsTable();
            
          
//            ArrayList<String> appIds = new ArrayList<>();
//            
//            appIds.add("985530");
//            appIds.add("1223");
//            appIds.add("424223");
//            appIds.add("5555");
//            appIds.add("992970");
//            appIds.add("993100");
//            
//            
//            Map<String, Boolean> results = DBQuery.hasRowsInTable(appIds, "idApp", "GAMES");
//            
//           for (String str : results.keySet()) {
//                if(results.get(str))
//                    System.out.println("Sim: " +str);
//                else
//                    System.out.println("Não: " +str);
//        }
            
        
//        Connection connection = null;
//        Statement statement = null;
//        ResultSet resultSet = null;
//        try {
//            connection = DBConnection.getConnection();
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery("SELECT * FROM GAMES WHERE idApp = '985530'");
//            
//            System.out.println(resultSet.isBeforeFirst());
//        } catch (SQLException ex) {
//            System.err.println("Erro: " +ex);
//        } finally{
//            DBConnection.closeConnection(connection, statement, resultSet);
//        }
          ArrayList<String> ids = new ArrayList<String>();
          Connection con = DBConnection.getConnection();
          Statement stm = null;
          ResultSet rs = null;
          
        try {
            stm = con.createStatement();
            
            rs = stm.executeQuery("SELECT PLAYER_ID FROM PLAYER");
            
            while(rs.next())
               ids.add(rs.getString("PLAYER_ID"));
            
            rs = stm.executeQuery("SELECT DISTINCT(PLAYER_ID) PLAYER_ID FROM PLAYER_OWNED_GAMES");
            
            while(rs.next())
               ids.remove(rs.getString("PLAYER_ID"));
            
            for (String id : ids)
                SteamOwnedGames.updateOwnedGames(id);
            
            
        } catch (SQLException ex) {

        } finally{
            DBConnection.closeConnection(con, stm, rs);
        }

        
    }
}
