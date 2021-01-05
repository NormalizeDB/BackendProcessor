package com.normalizedb.functions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RelationSchemaDeserializer extends JsonDeserializer<RelationSchema> {

    //TODO: Add request body structure validation

    @Override
    public RelationSchema deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(jsonParser);
        Iterator<JsonNode> attributeIterator = node.get("attributes").iterator();
        List<String> attrList = new LinkedList<>();
        while(attributeIterator.hasNext()){
            JsonNode currVal = attributeIterator.next();
            attrList.add(currVal.asText());
        }
        String[] attrArr = attrList.toArray(new String[0]);
        RelationSchema relationSchema = new RelationSchema(attrArr);

        Iterator<JsonNode> functionalDependencyIterator = node.get("functionalDependencies").iterator();
        while(functionalDependencyIterator.hasNext()){
            JsonNode currentFD = functionalDependencyIterator.next();
            List<String> keys = new LinkedList<>();
            List<String> derivations = new LinkedList<>();

            Iterator<JsonNode> keyIterator = currentFD.get("key").iterator();
            while(keyIterator.hasNext()){
                JsonNode currentKey = keyIterator.next();
                keys.add(currentKey.asText());
            }
            Iterator<JsonNode> derivIterator = currentFD.get("derivation").iterator();
            while(derivIterator.hasNext()){
                JsonNode currentDerivation = derivIterator.next();
                derivations.add(currentDerivation.asText());
            }

            relationSchema.addFunctionalDependency(keys.toArray(new String[0]), derivations.toArray(new String[0]));
        }

        return relationSchema;
    }
}
