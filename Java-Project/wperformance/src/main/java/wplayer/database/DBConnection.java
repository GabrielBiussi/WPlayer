package wplayer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author petter
 */
public class DBConnection {
    //private static final String HOSTNAME = "wplayer.database.windows.net";
    private static final String HOSTNAME = "servidor01191045.database.windows.net";
    //private static final String DBNAME = "bdWPlayer";
    private static final String DBNAME = "RaiseAdventurer";
    //private static final String USER = "wplayer";
    private static final String USER = "GF01191045";
    //private static final String PASSWORD = "#Gfgrupo9";
    private static final String PASSWORD = "#Gf46768871838";
    private static final String URL = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", HOSTNAME, DBNAME, USER, PASSWORD);
    
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao abrir Conexão: ", ex);
        }
    }
    
    public static void closeConnection(Connection connection){
        try {
            if(connection != null)
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Erro ao fechar Conexão:" +ex);
        }
    }
    
    public static void closeConnection(Connection connection, Statement statement){
        try {
            if(statement != null)
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Erro ao fechar Statement:" +ex);
        }
        
        closeConnection(connection);
    }
    
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet){
        try {
            if(resultSet != null)
            resultSet.close();
        } catch (SQLException ex) {
            System.err.println("Erro ao fechar ResultSet:" +ex);
        }
        
        closeConnection(connection, statement);
    }
    
}
