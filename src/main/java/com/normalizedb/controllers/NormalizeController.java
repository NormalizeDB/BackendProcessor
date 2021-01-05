package com.normalizedb.controllers;

import com.normalizedb.functions.RelationSchema;
import com.normalizedb.functions.CandidateKeyGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "normalize")
public class NormalizeController {

    @GetMapping(path = "/candidatekey",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String[]> getCandidateKeys(@RequestBody() RelationSchema relationSchema){
        String[] candidateKeys = CandidateKeyGenerator.generateCandidateKeys(relationSchema);
        return new ResponseEntity<>(candidateKeys, HttpStatus.OK);
    }

}
