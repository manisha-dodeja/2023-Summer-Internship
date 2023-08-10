package com.example.sampleendpoint;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class Metrics {
    String connectionString; //= "mongodb://localhost:27017";
    String database;
    String collection;
    Metrics(String connectionString, String database, String collection)
    {
        this.connectionString = connectionString;
        this.database = database;
        this.collection = collection;
    }
    public Map<Integer, Map<String, Double>> EstablishConnection(){

        //String connectionString = "mongodb://localhost:27017";
        //String connectionString  = "mongodb://athena:athena@dev-team-database-mongo-node3:27066";

        // Create a MongoClient instance
        MongoClient mongoClient = MongoClients.create(connectionString);



        // Access a database
        MongoDatabase db = mongoClient.getDatabase(database);
        MongoCollection<Document> mongoCollection = db.getCollection(collection);


        // Find a document
        FindIterable<Document> mongoDocuments = mongoCollection.find();
        MongoCursor<Document> cursor = mongoDocuments.cursor();


        // Print each document
        //Map<Integer, Document> mongoDataMap = new HashMap<>(); // But it is eventually going to be <Integer(AccountId), Document>
        //Map<Integer, Double> mongoDataMap = new HashMap<>(); // But it is eventually going to be <Integer(AccountId), Document>
        Map<Integer, Map<String, Double>> mongoDataMap = new HashMap<>(); // But it is eventually going to be <Integer(AccountId), Document>

        while (cursor.hasNext()) {
            Document document = cursor.next();

        //for (Document mongoDocument : mongoDocuments) {
            //ObjectId id = mongoDocument.getObjectId("id");
            //ObjectId id = mongoDocument.getObjectId("account_id.id");
            //int id = (int)mongoDocument.get("account_id.id");
            //int id = (int)document.get("account_id.id");
            //System.out.println( "this is account_id.id"+id);
            //ObjectId id = document.getObjectId("process_uuid");
            //System.out.println( document.toJson());

            //System.out.println(document.getClass().getSimpleName());
            //System.out.println(document.get("etl_type"));
            //System.out.println("if condition turns out be :"+ document.get("etl_type") != null && ((String)document.get("etl_type")).equals("AWS") );

            if(document.get("etl_type") != null && ((String)document.get("etl_type")).equals("AWS"))
            {

                //System.out.println(((Document)document.get("account_id")).get("id"));
                int id = (int)((Document)document.get("account_id")).get("id");
                Double cores = document.get("final_metrics") == null?  (Double)(((Document)document.get("etl_task")).get("coreEquivalent")):
                Math.max((Double)((Document)document.get("etl_task")).get("coreEquivalent") , (Double)((Document)((Document)document.get("final_metrics")).get("cores")).get("max"));
                int memory =  (int)(((Document)document.get("etl_task")).get("mbMemory"));
                //System.out.println(Math.ceil(1.124));
                //mongoDataMap.put(id, document);
                Map<String, Double> temp = new HashMap<>();
                //temp.put("cores", cores);
                if (false == (Boolean) document.get("out_of_memory"))
                {
                    temp.put("memory",memory+0.0);
                    temp.put("accountID", id+0.0);
                    mongoDataMap.put(id, temp);
                }



            }

            //System.out.println( id);


        }
        //print number od documents

        System.out.println("counting the total number of documents "+ mongoDataMap.size());

        // Print each document
        /*
        for (Integer key : mongoDataMap.keySet()) {
            Document document = mongoDataMap.get(key);
            System.out.println( document.toJson());

        }
        */
        /*for (Map.Entry<Integer, Map<String, Double>> entry : mongoDataMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }*/
        // Close the MongoDB connection
        mongoClient.close();
        //return "MongoDB task done";
        return mongoDataMap;

    }
}
