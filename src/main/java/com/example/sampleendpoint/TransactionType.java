package com.example.sampleendpoint;

import com.ca.sql.DataSources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.*;

public class TransactionType {
    public static Map<Integer, Map<String, Integer>> TypeQuery(Set<Integer> accountids) throws SQLException {
        DataSource SqlDataSource = DataSources.fetch("cwad","LM", "aws-etl", DataSources.SqlDriver.MICROSOFT_MSSQL);


        //Map<String,String> typeName = new HashMap<>();
        Map<String,Integer> temp = new HashMap<>();

        String[] keys = {
                "18", "33", "43", "13", "38", "32", "45", "15", "47", "51",
                "44", "1", "41", "31", "11", "26", "58", "3", "39", "30",
                "5", "37", "16", "48", "12", "22", "29", "56", "49", "42",
                "52", "46", "6", "21", "7", "20", "40", "10", "57", "53",
                "36", "19", "9", "59", "50", "8", "60", "17", "25", "35", "14"
        };

        for (String key : keys) {
            temp.put(key, 0);
        }

        Map<Integer, Map<String, Integer>> typeCount = new HashMap<>();

        if (SqlDataSource == null)
            //return "Connection Failed";
            return null;
        else {
            Connection m_Connection = SqlDataSource.getConnection();
            Statement m_Statement = m_Connection.createStatement();

            /*String query1 = "select distinct Id, Name from TransactionTypes";
            ResultSet m_ResultSet = m_Statement.executeQuery(query1);
            while (m_ResultSet.next()) {

                typeName.put(m_ResultSet.getString("Id"), m_ResultSet.getString("Name"));
                temp.put(m_ResultSet.getString("Name"), 0);


            }*/
            System.out.println("checkpoint 1 entry inside TypeQuery function ");
            /*String query1 = "select distinct transactiontypeid as Id from transactions ";
            ResultSet m_ResultSet = m_Statement.executeQuery(query1);
            while (m_ResultSet.next()) {

                temp.put(m_ResultSet.getString("Id"), 0);


            }*/
            System.out.println("initial temp" + temp);


            /*String query3 = "select distinct  accountid  from transactions ";
            ResultSet accountset = m_Statement.executeQuery(query3);
            System.out.println("accountset" + accountset);
        while(accountset.next()) {
            System.out.println("accountid" + accountset.getInt("accountid"));*/
        for(int account : accountids) {
            String query2 = "select accountid,transactiontypeid, count(transactiontypeid) as Count from (select accountid,transactiontypeid from transactions where accountid = " + account + " ) a  group by accountid, transactiontypeid";
            ResultSet ResultSet2 = m_Statement.executeQuery(query2);
            while (ResultSet2.next()) {

                if (typeCount.get(ResultSet2.getInt("accountid")) != null) {
                    //typeCount.get(ResultSet2.getInt("accountid")).put(typeName.get(ResultSet2.getString("transactiontypeid")), ResultSet2.getInt("Count"));
                    typeCount.get(ResultSet2.getInt("accountid")).put(ResultSet2.getString("transactiontypeid"), ResultSet2.getInt("Count"));

                } else {
                    //Map<String, Integer> temp = new HashMap<>();
                    //temp.put(ResultSet2.getString("transactiontypeid"), ResultSet2.getInt("Count"));
                    Map<String, Integer> targetMap = new HashMap<>();
                    targetMap.putAll(temp);
                    typeCount.put(ResultSet2.getInt("accountid"), targetMap);
                    //typeCount.get(ResultSet2.getInt("accountid")).put(typeName.get(ResultSet2.getString("transactiontypeid")), ResultSet2.getInt("Count"));
                    typeCount.get(ResultSet2.getInt("accountid")).put(ResultSet2.getString("transactiontypeid"), ResultSet2.getInt("Count"));

                }
                //System.out.println("typeCount" + typeCount);

            }
        }
            }
            return typeCount;




    }
}
