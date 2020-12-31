package com.normalizedb.controllers;

import com.normalizedb.functions.RelationSchema;
import com.normalizedb.functions.CandidateKeyGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NormalizeService {

    @GetMapping(value = "/normalize/candidatekey",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus()
    public ResponseEntity<String[]> getCandidateKeys(@RequestBody() RelationSchema relationSchema){
        String[] candidateKeys = CandidateKeyGenerator.generateCandidateKeys(relationSchema);
        return new ResponseEntity<>(candidateKeys, HttpStatus.OK);
    }

}
