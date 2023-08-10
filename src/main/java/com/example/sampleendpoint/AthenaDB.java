package com.example.sampleendpoint;

import com.ca.sql.DataSources;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AthenaDB {
    public static Map<Integer, Map<String, Double>> ConnectToAthena() throws SQLException {
        DataSource SqlDataSource = DataSources.fetch("cwad","athena_local", "athena", DataSources.SqlDriver.MICROSOFT_MSSQL);
        Map<Integer, Map<String, Double>> ETLParam = new HashMap<>(); // But it is eventually going to be <Integer(AccountId), Document>

        if (SqlDataSource == null)
            return null;
        else {


            Connection m_Connection = SqlDataSource.getConnection();
            Statement m_Statement = m_Connection.createStatement();
            String query = "select  accountId, coreEquivalent, mbMemory from AccountEtlParameters where etlTypeId = 1 ";
            ResultSet resultset = m_Statement.executeQuery(query);
            while (resultset.next()) {
                Map<String, Double> temp = new HashMap<>();
                int id = resultset.getInt("accountId");
                double coreEquivalent = resultset.getDouble("coreEquivalent");
                int mbMemory = resultset.getInt("mbMemory");
                temp.put("Cores",coreEquivalent );
                temp.put("Memory", mbMemory + 0.0);
                temp.put("accountID", id+0.0);
                ETLParam.put(id, temp);
                //System.out.println(resultset.getString("accountId"));
            }

            return ETLParam;



        }
    }
}
