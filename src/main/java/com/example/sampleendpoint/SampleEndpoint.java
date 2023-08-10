package com.example.sampleendpoint;

import com.ca.sql.DataSources;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.json.JsonWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
@Path("/athena")

public class SampleEndpoint {
    @GET
    @Path("/endpoint")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces("text/plain")

        // Make an HTTP POST request to the Flask APP
/*
        try {

            URL url = new URL("http://localhost:5000/build");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Write the JSON payload to the request body
            OutputStream os = conn.getOutputStream();
            os.write(payloadString.getBytes());
            os.flush();
            os.close();

            // Read the response
            String response = conn.getResponseMessage();
            System.out.println("Response: " + response);

            String responseBody = getResponseBody(conn);
            System.out.println("Response Body:");
            System.out.println(responseBody);
            // Cleanup
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // this function is acting as athena here
        public static String endpoint( @QueryParam("account") String queryParam) throws SQLException, IOException {
            Map<String,Integer> TransactionData;
            Map<String,Map<String,Integer>> LotsSecData;
            String Account = queryParam; //"190466";
            Set<Integer>  accountid = new HashSet<>();
            accountid.add(Integer.parseInt(Account));
            //DatabaseAccess SqlConnectionString1 =new DatabaseAccess("cwad","LM","aws-etl","Transactions");
            //DatabaseAccess SqlConnectionString2 =new DatabaseAccess("cwad","LM","aws-etl","Lots", Account);

            //TransactionData = SqlConnectionString1.EstablishConnection(accountid);
            TransactionData =Transaction.EstablishConnection(accountid,"Athena");
            //LotsSecData = Security.SecurityQuery(accountid);
            LotsSecData = SecurityMap.SecurityQuery(accountid,"Athena");
            //LotsData = SqlConnectionString2.EstablishConnection(null);

            JSONObject payload1 = new JSONObject();
            JSONArray columnNames1 = new JSONArray();
        //columnNames1.put(6405);
            columnNames1.put(TransactionData.get(Account));
        //columnNames1.put(1980);
            columnNames1.put(LotsSecData.get(Account).get("Lots"));
            //columnNames1.put(LotsSecData.get(Account).get("Security"));

            payload1.put("x_sample", columnNames1);
            String payloadString1 = payload1.toString();
            System.out.println(payloadString1);
            String responseBody = "No response";

        try {

            URL url = new URL("http://localhost:5000/predict");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Write the JSON payload to the request body
            OutputStream os = conn.getOutputStream();
            os.write(payloadString1.getBytes());
            os.flush();
            os.close();

            // Read the response
            String response = conn.getResponseMessage();
            System.out.println("Response: " + response);

            responseBody = getResponseBody(conn);
            System.out.println("Response Body:");
            System.out.println(responseBody);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody;
    }
    private static String getResponseBody(HttpURLConnection conn) throws IOException {
        InputStream inputStream = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

}
