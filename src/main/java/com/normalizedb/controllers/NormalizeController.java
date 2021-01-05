package com.normalizedb.controllers;

import com.normalizedb.functions.RelationSchema;
import com.normalizedb.functions.CandidateKeyGenerator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("normalize")
@Controller
public class NormalizeController {

    @POST
    @Path("/candidatekey")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public Response getCandidateKeys(RelationSchema relationSchema){
        String[] candidateKeys = CandidateKeyGenerator.generateCandidateKeys(relationSchema);
        return Response.ok().entity(candidateKeys).build();
    }

}
