import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RelationSchema {
    private Map<String, Integer> attributeMapping;
    private List<FunctionalDependency> integrityConstraints;
    private String[] attributes;

    public RelationSchema(String[] attributes){
        this.attributes = attributes;
        this.integrityConstraints = new ArrayList<>();
        attributeMapping = IntStream.range(0, attributes.length)
                .mapToObj(index -> new Object[]{attributes[index], index})
                .collect(Collectors.toMap(x -> String.valueOf(x[0]), y -> Integer.valueOf(String.valueOf(y[1]))));
    }

    public RelationSchema(String[] relation, List<FunctionalDependency> integrityConstraints){
        this(relation);
        this.integrityConstraints = integrityConstraints;
    }

    public void addFunctionalDependency(String[] keyVals, String[] derivations){
        FunctionalDependency newFD = new FunctionalDependency(toIndex(keyVals), toIndex(derivations));
        this.integrityConstraints.add(newFD);
    }


    /**
     * Converts functional dependency attribute names >> index values
     *
     * @param
     * @return
     */
    private Integer[] toIndex(String[] rawItemSet) {
        return Arrays.stream(rawItemSet)
                .map(x -> {
                    if (!attributeMapping.containsKey(x)) {
                        StringBuilder sb = new StringBuilder(String.format("Attribute %s is not within the relation: [", x));
                        for (int i = 0; i < attributes.length; i++) {
                            sb.append(String.format("%s", attributes[i]));
                            if (i != attributes.length - 1) {
                                sb.append(",");
                            }
                        }
                        sb.append("]");
                        throw new RuntimeException(sb.toString());
                    }
                    return attributeMapping.get(x);
                })
                .toArray(Integer[]::new);
    }

    /**
     * Retrieves all attributes for a relation
     * @return
     */
    public String[] getAttributes(){
        return attributes;
    }

    /**
     * Returns all integrity constraints
     * @return
     */
    public List<FunctionalDependency> getIntegrityConstraints(){
        return integrityConstraints;
    }
}
