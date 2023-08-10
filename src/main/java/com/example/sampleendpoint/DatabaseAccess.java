package com.example.sampleendpoint;


import com.ca.sql.DataSourceOptions;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import com.ca.sql.DataSources;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.*;



import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


//@Path("/database-access")
public class DatabaseAccess {
    private static DataSource SqlDataSource;
    String host;
    String database;
    String username;
    String table;

    String Account;


    DatabaseAccess(String host, String database, String username, String table) throws SQLException {
        this.host = host;
        this.database = database;
        this.username = username;
        this.table = table;


    }
    public Map<String,Integer> EstablishConnection(Set<Integer> accountids) throws SQLException {
        Map<String,Integer> data = new HashMap<>();
        List<String> queries = new ArrayList<>();
        int numThreads = 15; // You can adjust the number of threads based on your system and workload
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<ResultSet>> futures = new ArrayList<>();
        int i = 0;
        SqlDataSource = DataSources.fetch(host, database , username, DataSources.SqlDriver.MICROSOFT_MSSQL);

        //@GET
        //@Produces("text/plain")


        if (SqlDataSource == null)
            //return "Connection Failed";
            return null;
       /* else{
            System.out.println("Connection to "+ lmDataSource.getConnection() + " has been established");
        }*/
        else {
            try {
                Connection m_Connection = SqlDataSource.getConnection();
                //Statement m_Statement = m_Connection.createStatement();
                String query = "";
                int count = 0;
                for (int account : accountids) {
                    if (count > 50000)
                        break;
                    else {
                        query = "select AccountID, count(ID) as Count from (Select   AccountID, ID from " + table + " where AccountID =" + account + ") a group by  AccountID"; //="+ AccountID;
                        queries.add(query);
                        count ++;
                    }
                    /*ResultSet m_ResultSet = m_Statement.executeQuery(query);
                    while (m_ResultSet.next()) {
                        data.put(m_ResultSet.getString("AccountID"), m_ResultSet.getInt("Count"));
                    }*/
                    //System.out.println("Transaction Module Running for account" + account);
                }
                for (String quer : queries) {
                    Callable<ResultSet> task = () -> {
                        Statement m_Statement = m_Connection.createStatement();
                        ResultSet resultSet = m_Statement.executeQuery(quer);
                        return resultSet;
                    };
                    Future<ResultSet> future = executor.submit(task);
                    futures.add(future);
                }
                System.out.println("Number of tasks submitted to the executor: " + futures.size());

                for (Future<ResultSet> future : futures) {
                    future.get(); // Wait for the task to complete
                }
                System.out.println("size of futures: " + futures.size());

                // Wait for all tasks to complete and retrieve the result sets
                for (Future<ResultSet> future : futures) {
                    ResultSet resultSet = future.get();
                    System.out.println("ResultSet: " + resultSet);

                    // Process the result set as needed
                    if(resultSet != null) {
                        while (resultSet.next()) {
                            // Extract data from the result set and do something with it
                            // Example:
                            data.put(resultSet.getString("AccountID"), resultSet.getInt("Count"));

                            String columnValue = resultSet.getString("AccountID");
                            System.out.println("Value: " + columnValue);
                        }
                    }

                    // Close the result set after processing
                    resultSet.close();
                }
            } catch (SQLException | InterruptedException | ExecutionException e) {
                if (e instanceof ExecutionException && e.getCause() != null) {
                    if (e.getCause() instanceof NullPointerException) {
                        System.out.println("NullPointerException occurred during task execution:");
                        e.getCause().printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        }


            /*ResultSet m_ResultSet = m_Statement.executeQuery(query);

            while (m_ResultSet.next()) {
                //System.out.println("why are we not receiving any output suddenly");
                //System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
                data.put(m_ResultSet.getString("AccountID"),m_ResultSet.getInt("Count")) ;


            }*/

        //return "Connection to " + SqlDataSource + " has been established";
        return data;


    }
}