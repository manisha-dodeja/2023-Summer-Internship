package com.example.sampleendpoint;

import com.ca.sql.DataSources;
import com.google.gson.Gson;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SecurityMap {
    public static Map<String,Map<String,Integer>> SecurityQuery(Set<Integer> accountids, String Caller) throws SQLException {
        DataSource SqlDataSource = DataSources.fetch("cwad", "LM", "aws-etl", DataSources.SqlDriver.MICROSOFT_MSSQL);
        Map<String,Map<String,Integer>> data = new HashMap<>();
        for(int account : accountids){
            Map<String, Integer> temp = new HashMap<>();
            temp.put("Lots", 0);
            temp.put("Security", 0);

            data.put(account+"", temp);
        }

        if (SqlDataSource == null) {
        }

        //return "Connection Failed";
        //return null;
        else {
            Connection m_Connection = SqlDataSource.getConnection();
            Statement m_Statement = m_Connection.createStatement();
            if(Caller.equals("Athena"))
            {
                for (int account : accountids) {
                    String query = "select accountid, count(ID) as lots, count(distinct SecurityID) as security from (select accountid, ID, SecurityID from lots where accountid = " + account + " ) a  group by accountid";
                    ResultSet m_ResultSet = m_Statement.executeQuery(query);
                    while (m_ResultSet.next()) {
                        Map<String, Integer> temp = new HashMap<>();
                        temp.put("Lots", m_ResultSet.getInt("lots"));
                        temp.put("Security", m_ResultSet.getInt("security"));

                        data.put(m_ResultSet.getString("accountid"), temp);
                    }


                }
                return data;
            }


                //String query2 = "select accountid, count(ID) as lots, count(distinct SecurityID) as security from (select accountid, ID, SecurityID from lots where accountid = " + account + " ) a  group by accountid";
                String query1 = "select ID, accountid from lots  ";
                String query2 = "select distinct accountid, SecurityID from lots";
                System.out.println("Query1 Started"+ LocalTime.now());
                ResultSet resultSet1 = m_Statement.executeQuery(query1);

                System.out.println("Query1 Processed"+LocalTime.now());
                int i =0;
                while (resultSet1.next()) {
                    if (data.get(resultSet1.getString("accountid")) != null){
                        Map<String, Integer> innerMapToUpdate = data.get(resultSet1.getString("accountid"));
                        int val = innerMapToUpdate.get("Lots");
                        innerMapToUpdate.put("Lots",val+1);
                        data.put(resultSet1.getString("accountid"), innerMapToUpdate);
                        if (i%1000 == 0){
                            System.out.println(i);
                        }

                        i++;

                    }
                }
            System.out.println("Lots Done"+LocalTime.now());
            System.out.println("Query2 Started"+ LocalTime.now());
            ResultSet resultSet2 = m_Statement.executeQuery(query2);
            System.out.println("Query2 Started"+ LocalTime.now());

            i = 0;
                    while (resultSet2.next()) {
                        if (data.get(resultSet2.getString("accountid")) != null){
                            Map<String, Integer> innerMapToUpdate = data.get(resultSet2.getString("accountid"));
                            int val = innerMapToUpdate.get("Security");
                            innerMapToUpdate.put("Security",val+1);
                            data.put(resultSet2.getString("accountid"), innerMapToUpdate);
                            if (i%1000 == 0){
                                System.out.println(i);
                            }

                            i++;


                        }

                }
            System.out.println("Security Done"+LocalTime.now());
            Gson gson = new Gson();
            String json = gson.toJson(data);

            // Write JSON to a file
            String path = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\lotsec.json";
            try (FileWriter writer = new FileWriter(path)) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }






        }
        return data;



    }
}


