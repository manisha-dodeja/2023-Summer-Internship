package com.example.sampleendpoint;

import com.ca.sql.DataSources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Security {
    public static Map<String,Map<String,Integer>> SecurityQuery(Set<Integer> accountids) throws SQLException {
        DataSource SqlDataSource = DataSources.fetch("cwad", "LM", "aws-etl", DataSources.SqlDriver.MICROSOFT_MSSQL);
        Map<String,Map<String,Integer>> data = new HashMap<>();

        if (SqlDataSource == null) {
        }

        //return "Connection Failed";
        //return null;
        else {
            Connection m_Connection = SqlDataSource.getConnection();
            Statement m_Statement = m_Connection.createStatement();

            for (int account : accountids) {
                String query2 = "select accountid, count(ID) as lots, count(distinct SecurityID) as security from (select accountid, ID, SecurityID from lots where accountid = " + account + " ) a  group by accountid";
                ResultSet m_ResultSet = m_Statement.executeQuery(query2);
                while (m_ResultSet.next()) {
                    Map<String, Integer> temp = new HashMap<>();
                    temp.put("Lots", m_ResultSet.getInt("lots"));
                    temp.put("Security", m_ResultSet.getInt("security"));

                    data.put(m_ResultSet.getString("accountid"), temp);
                }
                System.out.println("Security Module Running for account" + account);


            }



        }
        return data;



    }
}


