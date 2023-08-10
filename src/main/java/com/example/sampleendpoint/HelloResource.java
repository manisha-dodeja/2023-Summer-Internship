package com.example.sampleendpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.sql.SQLException;

@Path("/hello-world")
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello() throws SQLException {

        return "Hello, World!";
        //return BasisWS.postRequest(AthenaDB.ConnectToAthena().keySet()).toString();
    }
}