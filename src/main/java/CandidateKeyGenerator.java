import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
        CandidateKeyGenerator ckg = new CandidateKeyGenerator(new String[]{"A", "B", "C", "D", "E"});
        ckg.generateCandidateKeys(new String[][][]{
                {{"D","B","C"}, {"E"}},
                {{"D","B"}, {"E"}},
                {{"A", "B"}, {"C", "B"}},
//                {{"A","D"},{"C"}},
//                {{"A","C"},{"B"}},
                {{"A"},{"D"}}
        });
    }

    public String generateCandidateKeys(String[][][] functionalDependencies){
        Integer[][][] standardizedFDs = convertFunctionalDependencies(functionalDependencies);
        Integer[] res = findEssentialAttributes(standardizedFDs);
        determineCandidateKeys(res, standardizedFDs);

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
    private Integer[] findEssentialAttributes(Integer[][][] functionalDependencies) {
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
        Integer[] finalRes = new Integer[mandatoryAttributes.size()];
        for(int i = 0; i < mandatoryAttributes.size(); i++){
            finalRes[i] = mandatoryAttributes.get(i);
        }
        return finalRes;
    }

    private Integer[][] determineCandidateKeys(Integer[] essentialAttr, Integer[][][] functionalDependencies) {
        //Sort FDs based on length of keys (left side)
        Arrays.sort(functionalDependencies, Comparator.comparingInt(x -> x[0].length));

        System.out.println(testCandidateKey(essentialAttr, functionalDependencies));
        return null;
    }

    private boolean testCandidateKey(Integer[] currentCK, Integer[][][] functionalDependencies) {
        //ASSUMPTION: FD keys are sorted by length
        Set<Integer> ckAttrSet = new TreeSet<>();
        ckAttrSet.addAll(Arrays
                .stream(currentCK)
                .collect(Collectors.toSet()));

        List<Integer> unmatched = new LinkedList<>();
        for (int i = 0; i < functionalDependencies.length; i++) {
            unmatched.add(i);
        }
        boolean limitReached = false;

        while(!limitReached) {

            for (Integer i : unmatched) {
                //Grab left side of FD
                Set<Integer> currentFD = new HashSet<>();
                currentFD.addAll(
                        Arrays.stream(functionalDependencies[i][0])
                                .collect(Collectors.toSet())
                );
                //If our CK's length is less than the FD's length, we break. Since the FD array is sorted,
                //we can assume all proceeding FDs will be larger as well
                if(currentFD.size() > ckAttrSet.size()){
                    limitReached = true;
                    break;
                }
                currentFD.removeAll(ckAttrSet);
                //This would indicate that from our key set, we are able to derive another attribute; we
                //add it to our original set above
                if (currentFD.isEmpty()) {
                    ckAttrSet.addAll(Arrays.stream(functionalDependencies[i][1])
                            .collect(Collectors.toSet()));
                    unmatched.remove(i);
                    //go back to the unmatched FDs whose size < ckKeySize, so see if anything changed
                    break;
                }
            }
            if(unmatched.isEmpty()){
                break;
            }
        }
        //Whether our attribute set is equivalent to our current derived CK set
        return unmatched.isEmpty();
    }

    private String[] convertCandidateKeys(int[][] candidateKeys) {
        return null;
    }
}
