package com.example.sampleendpoint;

import com.ca.sql.DataSources;
import com.google.gson.Gson;
import org.json.JSONObject;

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

public class Transaction {

    public static Map<String,Integer> EstablishConnection(Set<Integer> accountids, String Caller) throws SQLException {
        DataSource SqlDataSource = DataSources.fetch("cwad","LM", "aws-etl", DataSources.SqlDriver.MICROSOFT_MSSQL);
        Map<String, Integer> data = new HashMap<>();
        for(int account : accountids){

            data.put(account+"", 0);
        }

        if (SqlDataSource == null)
            return null;

        else {
            Connection m_Connection = SqlDataSource.getConnection();
            Statement m_Statement = m_Connection.createStatement();
            String query = "";
            if (Caller.equals("Athena"))
            {
                for (int account : accountids) {
                    query = "select AccountID, count(ID) as Count from (Select   AccountID, ID from  Transactions where AccountID =" + account + ") a group by  AccountID"; //="+ AccountID;
                    ResultSet m_ResultSet = m_Statement.executeQuery(query);
                    while (m_ResultSet.next()) {
                        data.put(account + "",m_ResultSet.getInt("Count") );
                    }


                }
                return data;

            }

            System.out.println("Query Execution Started"+LocalTime.now());
            query = "select AccountID, ID from Transactions ";
            ResultSet m_ResultSet = m_Statement.executeQuery(query);
            System.out.println("Query Execution Done"+ LocalTime.now());
            int i = 0;
            while (m_ResultSet.next()) {
                if (data.get(m_ResultSet.getString("AccountID")) != null)
                {
                    int val = data.get(m_ResultSet.getString("AccountID"));
                    /*if (val > 0){
                        System.out.println("Account : "+ m_ResultSet.getString("AccountID")+" Val :"+ val);
                    }*/

                    data.put(m_ResultSet.getString("AccountID"), val+1);
                    /*if (i%1000 == 0){
                        System.out.println(i);
                    }

                    i++;*/

                }


            }
            System.out.println("Transaction Done"+LocalTime.now());

            //JSONObject jsonObject = new JSONObject(data);

            // Write the JSONObject to a file
            Gson gson = new Gson();
            String json = gson.toJson(data);

            // Write JSON to a file
            String path = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\transc.json";
            try (FileWriter writer = new FileWriter(path)) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return data;
        }

    }
