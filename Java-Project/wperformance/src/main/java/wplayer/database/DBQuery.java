package wplayer.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author petter
 */
public class DBQuery {
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
                System.err.println("Erro (hasRowsInTable -ELSE): " +ex);
            }finally{
                DBConnection.closeConnection(connection, statement, resultSet);
            }
            
        }
            return null;
    }
}
