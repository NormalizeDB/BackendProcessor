import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CandidateKeyGenerator {
    private Map<String, Integer> attributeMapping;

    public CandidateKeyGenerator(String[] relation){
        attributeMapping = IntStream.range(0, relation.length)
                .mapToObj(index -> new Object[]{relation[index], index})
                .collect(Collectors.toMap(x -> String.valueOf(x[0]), y -> Integer.valueOf(String.valueOf(y[1]))));
    }

    public static void main(String[] args) {
        //Temporary Tests
        CandidateKeyGenerator ckg = new CandidateKeyGenerator(new String[]{"A", "B", "C", "D"});
        ckg.generateCandidateKeys(new String[][][]{
                {{"A","B","C"}, {"D"}},
                {{"C"}, {"A"}}
        });
    }

    public String generateCandidateKeys(String[][][] functionalDependencies){
        Integer[][][] standardizedFDs = convertFunctionalDependencies(functionalDependencies);
        int[] res = findEssentialAttributes(standardizedFDs);
        for(int i: res){
            System.out.println(i);
        }
        return null;
    }

    /**
     * Converts an list of functional dependencies from attribute names >> index values
     *
     * @param functionalDependencies set of FDs
     * @return
     */
    private Integer[][][] convertFunctionalDependencies(String[][][] functionalDependencies) {
        List<Integer[][]> convertedArr = Arrays.stream(functionalDependencies).map(x -> {
            List<Integer[]> convertedFD = Arrays.stream(x).map(y -> {
                Integer[] tempIndiciesCollec = new Integer[y.length];
                //So here, we're going to convert traditional attributes into a concat of their indicies
                //in terms of their relational schema
                for(int i = 0; i < y.length; i++){
                    tempIndiciesCollec[i] = attributeMapping.get(y[i]);
                }
                return tempIndiciesCollec;
            }).collect(Collectors.toList());
            return convertedFD.toArray(new Integer[convertedFD.size()][]);
        }).collect(Collectors.toList());
        return convertedArr.toArray(new Integer[convertedArr.size()][][]);
    }

    /**
     * Determines the lower bound of the # of attributes necessary construct a candidate key
     * @param functionalDependencies
     * @return
     */
    private int[] findEssentialAttributes(Integer[][][] functionalDependencies) {
        //First we're going to simply determine whether the set of FDs contains all of the attr in the relation
        Set<Integer> attrSet = new HashSet(attributeMapping.values().stream().collect(Collectors.toSet()));

        Set<Integer> betaSet = Arrays.stream(functionalDependencies)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
        Set<Integer> primaryEssentialSet = new HashSet<>(attrSet);
        primaryEssentialSet.removeAll(betaSet);

        Set<Integer> alphaSet = Arrays.stream(functionalDependencies)
                .map(x -> x[1])
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
        Set<Integer> secondaryEssentialSet = new HashSet<>(attrSet);
        secondaryEssentialSet.removeAll(alphaSet);

        primaryEssentialSet.addAll(secondaryEssentialSet);

        List<Integer> mandatoryAttributes = new LinkedList<>(primaryEssentialSet);
        int[] finalRes = new int[mandatoryAttributes.size()];
        for(int i = 0; i < mandatoryAttributes.size(); i++){
            finalRes[i] = mandatoryAttributes.get(i);
        }
        return finalRes;
    }

    private int[] determineCandidateKeys(int[] essentialAttr){
        //TODO: Implement
        return null;
    }
}
