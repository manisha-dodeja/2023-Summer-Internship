package com.example.sampleendpoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.bson.json.JsonWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Singleton;

import java.sql.SQLException;
import java.util.*;
@Path("/modeldata")
public class ModelData {
    @GET
    @Path("/java-endpoint")
    //@Produces(MediaType.APPLICATION_JSON)
    @Produces("text/plain")

    public static String requesthandle() throws SQLException, IOException {


        // create a json file out of listOfMaps
        /*ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("failedtasks.json"), listOfMaps);
            System.out.println("List of maps converted to JSON and written to output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //-------------------------------------------------------
        String jsonContent = "";
        JSONArray listOfMaps = null;
        String filePath = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\finalrun.json";
        try {
            if (Files.exists(Paths.get(filePath))) {
                jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
                listOfMaps = new JSONArray(jsonContent);
                System.out.println("Inside if" + listOfMaps);


            }
            else{
                listOfMaps = access_from_db();


                try {
                    FileWriter myWriter = new FileWriter(filePath);
                    myWriter.write(listOfMaps.toString());
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // send a request to flask service and pass the json object

        JSONObject payload = new JSONObject();
        JSONArray columnNames = new JSONArray();
        columnNames.put("Transaction");
        columnNames.put("Lots");
        //columnNames.put("Security");
        payload.put("x_column", columnNames);
        payload.put("y_column", "Memory");
        payload.put("data", listOfMaps);
        String payloadString = payload.toString();
        return payloadString;

    }
    public static JSONArray access_from_db() throws SQLException {
        Map<Integer, Map<String, Double>> InputData;

        // step 1 :establish connection to MongoDb
/*        String MongoDBConnectionString =  "mongodb://athena:athena@dev-team-database-mongo-node3:27066";//"mongodb://localhost:27017";
        String MongoDatabase =  "etl-dispatcher";//"MetricDatabase"
        String MongoCollection = "job_metrics"; //"metrics"

        Metrics metrics = new Metrics(MongoDBConnectionString,MongoDatabase,MongoCollection  );
        InputData = metrics.EstablishConnection();
        Set<Integer>accountids = InputData.keySet();*/
        InputData = AthenaDB.ConnectToAthena();
        Set<Integer>accountids = InputData.keySet();
        System.out.println("size of accountids set: " + accountids.size());



        // step 2 : establish connection to SQL server
        Map<String,Integer> TransactionData = new HashMap<>();
        Map<String,Integer> LotsData;
        Map<String,Map<String, Integer>> SecurityData = new HashMap<>();
        Map<Integer, Map<String, Integer>> TypeCount = new HashMap<>(); //New Changes
        Map<String, Integer> Basis = new HashMap<>();
        String host ="cwad";
        String database = "LM";
        String username = "aws-etl";

        DatabaseAccess SqlConnectionString1 =new DatabaseAccess(host,database,username,"Transactions");
        DatabaseAccess SqlConnectionString2 =new DatabaseAccess(host,database,username,"Lots");

        /*TransactionData = SqlConnectionString1.EstablishConnection(accountids);
        System.out.println("---------------------TRANSACTION DATA -------------------------");
        System.out.println(TransactionData);*/

        //TransactionData = Transaction.EstablishConnection(accountids);
        System.out.println("---------------------TRANSACTION DATA -------------------------");
        String TrancfilePath = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\transc.json";

        // Create ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();

        // Read JSON file and convert it to HashMap
        try {
            if(Files.exists(Paths.get(TrancfilePath))) {
                File file = new File(TrancfilePath);
                TransactionData = mapper.readValue(file, HashMap.class);
            }
            else {
                TransactionData = Transaction.EstablishConnection(accountids,"Train");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(TransactionData);

        //System.out.println("---------------------TRANSACTION DATA -------------------------");
        //System.out.println(TransactionData);

        /*LotsData = SqlConnectionString2.EstablishConnection(accountids);
        System.out.println("---------------------LOTS DATA -------------------------");
        System.out.println(LotsData);*/

        /*TypeCount = TransactionType.TypeQuery(accountids);
        System.out.println("---------------------TYPECOUNT DATA -------------------------");
        System.out.println(TypeCount);*/
        // Uncomment from here
        /*SecurityData = Security.SecurityQuery(accountids);
        System.out.println("---------------------LOTS & SECURITY DATA -------------------------");
        System.out.println(SecurityData);
*/

        System.out.println("---------------------LOTS & SECURITY DATA -------------------------");
        String LotsSecfilePath = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\lotsec.json";

        try {
            if(Files.exists(Paths.get(LotsSecfilePath))) {
                File file = new File(LotsSecfilePath);
                SecurityData =
                        mapper.readValue(file, new TypeReference<Map<String, Map<String, Integer>>>() {});

            }
            else{
                SecurityData = SecurityMap.SecurityQuery(accountids,"Train");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(SecurityData);

        System.out.println("---------------------BASIS DATA -------------------------");

        String BasisfilePath = "C:\\Users\\mdodeja\\IdeaProjects\\SampleEndpoint\\Basis.json";
        try {
            if(Files.exists(Paths.get(BasisfilePath))) {
                File file = new File(BasisfilePath);
                Basis = mapper.readValue(file, HashMap.class);
            }
            else {
                Basis = BasisWS.postRequest(accountids, "Train");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Basis);


        for (Integer key : InputData.keySet()){
            if  (TransactionData.get(key+"") != null && SecurityData.get(key+"") != null) //last boolean is New && TypeCount.get(key)!=null && LotsData.get(key+"") != null
            //if (TypeCount.get(key)!=null)
            {
                InputData.get(key).put("Transaction", TransactionData.get(key+"") + 0.0);
                //InputData.get(key).put("Lots", LotsData.get(key+"") + 0.0);
                //InputData.get(key).put("Security", SecurityData.get(key+"") + 0.0);

                for (Map.Entry<String, Integer> entry : SecurityData.get(key+"").entrySet())
                {
                    InputData.get(key).put(entry.getKey(), entry.getValue() + 0.0);
                }
                //New Stuff
               /* for (Map.Entry<String, Integer> entry : TypeCount.get(key).entrySet())
                {
                    InputData.get(key).put(entry.getKey(), entry.getValue() + 0.0);
                }*/
                if(Basis.get(key+"") != null) {
                    InputData.get(key).put("Basis", Basis.get(key+"") + 0.0);
                }
                else{

                    InputData.get(key).put("Basis",  0.0);
                }




            }
        }
        //List<Map<String, Double>> listOfMaps = new ArrayList<>();
        //List<Map<String, Double>> listOfMaps = new ArrayList<>();
        JSONArray listOfMaps = new JSONArray();

        for (Integer key : InputData.keySet()){
            if( InputData.get(key).get("Transaction") != null && InputData.get(key).get("Lots") != null && InputData.get(key).get("Security") != null)
            //if (InputData.get(key)!= null)
            {
                listOfMaps.put(InputData.get(key));
                //System.out.println("-------- Elements that should go in listofMaps");
                //System.out.println(InputData.get(key));
            }
        }

        System.out.println(listOfMaps);
        return listOfMaps;
        //System.out.println(listOfMaps);

    }
}
