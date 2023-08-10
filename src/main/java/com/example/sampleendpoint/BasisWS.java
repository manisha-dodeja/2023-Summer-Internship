package com.example.sampleendpoint;
import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
//import java.net.http.HttpClient;
import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class BasisWS {
    /*    public  static int request(int accountID) {
            // Create an HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Create a request object with the URL of the web service
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://dev-aws1-service-tomcats2-app200.arbfund.com:8084/basis-ws/v2/bases?accountId="+accountID+"&"))
                    .build();
            int basiscount = 0;

            try {
                // Send the request and get the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Process the response
                int statusCode = response.statusCode();
                String responseBody = response.body();
                JSONArray jsonArray = new JSONArray(responseBody);

                System.out.println("Status code: " + statusCode);
                System.out.println("Response body: " + responseBody);
                basiscount = jsonArray.length();
                System.out.println("basis count: " + basiscount);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return basiscount;
        }*/
/*
    public static Map<String, Integer> postRequest(Set<Integer> accountids) {
        String payloadString1 = "";
        String responseBody = "";
        for(int account:accountids)
        {
            payloadString1 += "accountId="+account+"&";
        }
        //System.out.println(payloadString1);

        try {

            URL url = new URL("http://dev-aws1-service-tomcats2-app200.arbfund.com:8084/basis-ws/v2/bases/map");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain");
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
        Gson gson = new Gson();
        Map<String, Integer>Basis = new HashMap<>();
        Map<String, int[]> keyValueMap = gson.fromJson(responseBody, Map.class);

        for (Map.Entry<String, int[]> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            int arraySize = entry.getValue().length;
            Basis.put(key,arraySize);
        }
        return Basis;
    }
*/
/*
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
*/
    public static Map<String, Integer> postRequest(Set<Integer> accountids, String Caller) {
    //public static String postRequest(Set<Integer> accountids) {

        String responseBody = "";
        StringBuilder response = new StringBuilder();

        /*try {
            URL url = new URL("http://dev-aws1-service-tomcats2-app200.arbfund.com:8084/basis-ws/v2/bases/map");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            conn.setRequestMethod("POST");

            // Set the request headers (optional)
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Enable output and disable caching
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            int a[] = accountids.stream().mapToInt(Integer::intValue).toArray();
            // Build the payload as a JSON object
            JSONObject payload = new JSONObject();
            payload.put("accountId", a); // Replace "key" with the actual key name in your API

            // Convert the JSON object to a string and write the request body
            String requestBody = payload.toString();
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();


            // ... Continue with the response handling as before ...

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response
            System.out.println("Response Code: " + responseCode);
            System.out.println("Response Body: " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*Gson gson = new Gson();
        Map<String, Integer>Basis = new HashMap<>();
        Map<String, int[]> keyValueMap = gson.fromJson(responseBody, Map.class);

        for (Map.Entry<String, int[]> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            int arraySize = entry.getValue().length;
            Basis.put(key,arraySize);*/

        String url = "http://dev-aws1-service-tomcats2-app200.arbfund.com:8084/basis-ws/v2/bases/map";

        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            // Set the request method to POST
            conn.setRequestMethod("POST");

            // Set the request headers
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Accept", "application/json");

            // Enable output and disable caching
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            // Construct the form data
            String accountIdField = "accountId";
            //int[] values = {"123", "456", "789"}; // Replace with the actual values you want to send
            int[] values = accountids.stream().mapToInt(Integer::intValue).toArray();


            StringBuilder formData = new StringBuilder();
            for (int value : values) {
                formData.append(accountIdField).append("=").append(value).append("&");
            }

            // Remove the last "&" character

            // Convert the form data to bytes and write it to the request body
            byte[] postDataBytes = formData.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();

            // Get the response (you can continue with response handling as needed)
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, int[]>>(){}.getType();

        Map<String, Integer> Basis = new HashMap<>();
        Map<String, int[]> keyValueMap = gson.fromJson(response.toString(), type);

        for (Map.Entry<String, int[]> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            int arraySize = entry.getValue().length;
            Basis.put(key, arraySize);
            //return response.toString();
        }

        String json = gson.toJson(Basis);
        String path = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\Basis.json";
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Basis;

    }
}




