/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static final String hostName = "servidor01191045.database.windows.net";
    private static final String dbName = "RaiseAdventurer";
    private static final String user = "GF01191045";
    private static final String password = "#Gf46768871838";
    private static final String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);
    
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new RuntimeException("Erro: ", ex);
        }
    }
    
    public static void closeConnection(Connection connection){
        try {
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Erro:" +ex);
        }
    }
    public static void closeConnection(Connection connection, Statement statement){
        try {
            statement.close();
        } catch (SQLException ex) {
            System.err.println("Erro:" +ex);
        }
        
        closeConnection(connection);
    }
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet){
        try {
            resultSet.close();
        } catch (SQLException ex) {
            System.err.println("Erro:" +ex);
        }
        
        closeConnection(connection, statement);
    }
    
}
