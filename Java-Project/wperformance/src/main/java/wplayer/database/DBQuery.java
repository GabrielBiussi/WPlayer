package wplayer.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import wplayer.logtxt.CriarLog;

/**
 *
 * @author petter
 */
public class DBQuery {
    
    public static ArrayList<String> getGamesIds(){
        ArrayList<String> gamesIds = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        final String TABLE = "APPS";
        final String FIELD = "APP_ID";
        final String TYPE = "game";
        
        String query = String.format(
                       "SELECT "
                          + "%s "
                       + "FROM %s "
                      + "WHERE APP_TYPE = '%s'", FIELD, TABLE, TYPE);
        
        try{
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(query);
            
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next())
                gamesIds.add(resultSet.getString(FIELD));
            
            return gamesIds;
  
        }catch(SQLException ex){
            CriarLog.WriteLog("Erro!Falha na conexão ao SQL: "+ex);
            System.err.println("Erro SQL: "+ex);
        }finally{
            DBConnection.closeConnection(connection, preparedStatement, resultSet);
        }
        
        return gamesIds;
    }
    
    public static Boolean hasRowInTable(String row, String field, String table){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
            
        String query = String.format("SELECT"
                                          + " * "
                                     + "FROM %s "
                                    + "WHERE %s = '%s'", table, field, row);
        
        try {
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            
            resultSet = statement.executeQuery(query);
            
            return resultSet.isBeforeFirst();
                
        } catch (SQLException ex) {
            CriarLog.WriteLog("Erro! Falha com (hasRowInTable): "+ex);
            System.err.println("Erro (hasRowInTable): "+ex);
        } finally{
            DBConnection.closeConnection(connection, statement, resultSet);
        }
        
        return null;
    }
    
    public static Map<String, Boolean> hasRowsInTable(ArrayList<String> rows, String field, String table){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Map<String, Boolean> listRows = new HashMap<String, Boolean>();
        ArrayList<String> containList = new ArrayList<String>();
        String records = "";
        
        if(rows.toArray().length < 10){

            for(String row : rows)
                records += String.format("'%s',", row);

            records = records.substring(0, records.length() - 1);

            String query = String.format("SELECT"
                                              + " * "
                                         + "FROM %s "
                                        + "WHERE %s IN (%s)", table, field, records);

            try{
               connection = DBConnection.getConnection();
               statement = connection.createStatement();

               resultSet = statement.executeQuery(query);

               while(resultSet.next()){
                  containList.add(resultSet.getString(field));
               }

               for(String row : rows)
                   listRows.put(row, containList.contains(row));

               return listRows;

            }catch(SQLException ex){
                CriarLog.WriteLog("Erro! Falha com (hasRowsInTable -IF): " +ex);
                System.err.println("Erro (hasRowsInTable -IF): " +ex);
            }finally{
                DBConnection.closeConnection(connection, statement, resultSet);
            }
        }
        
        else{
            String query = String.format("SELECT"
                                              + " %s "
                                         + "FROM %s", field, table);
            
            try{
               connection = DBConnection.getConnection();
               statement = connection.createStatement();

               resultSet = statement.executeQuery(query);

               while(resultSet.next()){
                  containList.add(resultSet.getString(field));
               }

               for(String row : rows)
                   listRows.put(row, containList.contains(row));

               return listRows;

            }catch(SQLException ex){
                CriarLog.WriteLog("Erro! Falha com (hasRowsInTable -ELSE): " +ex);
                System.err.println("Erro (hasRowsInTable -ELSE): " +ex);
            }finally{
                DBConnection.closeConnection(connection, statement, resultSet);
            }
            
        }
            return null;
    }
}
