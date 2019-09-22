/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Acer
 */
public class DBQuery {
    public static Boolean hasRowInTable(String record, String field, String table){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        String query = String.format("SELECT"
                                   +         "*"
                                   +   "FROM %s"
                                   +  "WHERE %s = '%s'", table, field, record);
            
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            
            resultSet = statement.executeQuery(query);
            
            return resultSet.isBeforeFirst();
                
        } catch (SQLException ex) {
            System.err.println("Erro na criação do statement: "+ex);
        } finally{
            DBConnection.closeConnection(connection, statement, resultSet);
        }
        
        return null;
    }
}
