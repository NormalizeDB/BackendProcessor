import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CandidateKeyGeneratorTests {

    private static Map<RelationSchema, String[]> expectationMapping;

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @BeforeClass
    public static void collectSamples() {
        expectationMapping = new LinkedHashMap<>();
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("sampleFunctionalDependencies.json");
            FileReader sampleFile = new FileReader(url.getPath());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(sampleFile);
            JsonNode collection = root.get("samples");
            if (collection.isArray()) {
                for (JsonNode testCase : collection) {
                    List<String> attributeCollec = new ArrayList<>();
                    for (JsonNode attr : testCase.get("schema")) {
                        attributeCollec.add(attr.asText());
                    }
                    RelationSchema schema = new RelationSchema(attributeCollec.toArray(new String[0]));
                    for (JsonNode fd : testCase.get("fds")) {
                        List<String> keys = new ArrayList<>();
                        for (JsonNode key : fd.get("key")) {
                            keys.add(key.asText());
                        }
                        List<String> derivations = new ArrayList<>();
                        for (JsonNode derivation : fd.get("derivation")) {
                            derivations.add(derivation.asText());
                        }
                        schema.addFunctionalDependency(keys.toArray(new String[0]), derivations.toArray(new String[0]));
                    }
                    List<String> expectedVals = new ArrayList<>();
                    for (JsonNode expectedVal : testCase.get("expected")) {
                        expectedVals.add(expectedVal.asText());
                    }
                    expectationMapping.put(schema, expectedVals.toArray(new String[0]));
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void validCandidateKeyTest() {
        int sampleCount = 1;
        for (RelationSchema rs : CandidateKeyGeneratorTests.expectationMapping.keySet()) {
            try {
                String[] actualResponse = CandidateKeyGenerator.generateCandidateKeys(rs);
                String[] expectedResponse = CandidateKeyGeneratorTests.expectationMapping.get(rs);
                //sort/standardize the responses
                assertEquals(String.format("[Sample #: %d]\tThe actual response length was %d, whereas the expected response length was %d", sampleCount, actualResponse == null ? 0 : actualResponse.length, expectedResponse.length)
                        , expectedResponse.length, actualResponse == null ? 0 : actualResponse.length);
                for (int i = 0; i < expectedResponse.length; i++) {
                    char[] tempArr = actualResponse[i].toCharArray();
                    Arrays.sort(tempArr);
                    actualResponse[i] = String.valueOf(tempArr);

                    tempArr = expectedResponse[i].toCharArray();
                    Arrays.sort(tempArr);
                    expectedResponse[i] = String.valueOf(tempArr);
                }
                if (actualResponse != null) {
                    Arrays.sort(actualResponse);
                }
                Arrays.sort(expectedResponse);
                for (int i = 0; i < expectedResponse.length; i++) {
                    String message = String.format("[Sample #: %d]\tActual CK: %s.....Expected CK: %s", sampleCount, actualResponse[i], expectedResponse[i]);
                    assertEquals(message, expectedResponse[i], actualResponse[i]);
                }
            } catch (AssertionError ex) {
                errorCollector.addError(ex);
            }
            sampleCount++;
        }
    }

    @Test
    public void invalidEmptySchemaTest() {
        //empty schema
        RelationSchema rs = new RelationSchema(new String[0]);
        assertThrows(RuntimeException.class,
                () -> rs.addFunctionalDependency(new String[]{"A"}, new String[]{"B"}));
    }

    @Test
    public void invalidFDKeyTest() {
        RelationSchema rs = new RelationSchema(new String[]{"A", "B", "C"});
        assertThrows(RuntimeException.class,
                () -> rs.addFunctionalDependency(new String[]{"X"}, new String[]{"A", "B"}));
    }

    @Test
    public void invalidFDDerviationTest() {
        RelationSchema rs = new RelationSchema(new String[]{"A", "B", "C"});
        assertThrows(RuntimeException.class,
                () -> rs.addFunctionalDependency(new String[]{"A", "B"}, new String[]{"X"}));
    }

}
