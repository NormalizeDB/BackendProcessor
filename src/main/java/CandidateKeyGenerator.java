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
    private Map<Integer, String> reversedAttrMapping;

    public CandidateKeyGenerator(String[] relation){
        attributeMapping = IntStream.range(0, relation.length)
                .mapToObj(index -> new Object[]{relation[index], index})
                .collect(Collectors.toMap(x -> String.valueOf(x[0]), y -> Integer.valueOf(String.valueOf(y[1]))));
        reversedAttrMapping = IntStream.range(0, relation.length)
                .mapToObj(index -> new Object[]{relation[index], index})
                .collect(Collectors.toMap(x -> Integer.valueOf(String.valueOf(x[1])), y -> String.valueOf(y[0])));
    }

    public static void main(String[] args) {
        //Temporary Tests
        CandidateKeyGenerator ckg = new CandidateKeyGenerator(new String[]{"A", "B", "C", "D"});
        String[] res = ckg.generateCandidateKeys(new String[][][]{
                {{"A", "B"}, {"C"}},
                {{"C"}, {"D"}},
                {{"D"}, {"A"}}
        });
        for(String ck: res){
            System.out.println(ck);
        }
    }

    public String[] generateCandidateKeys(String[][][] functionalDependencies){
        Integer[][][] standardizedFDs = convertFunctionalDependencies(functionalDependencies);
        Integer[] res = findEssentialAttributes(standardizedFDs);
        String[] candidateKeys = determineCandidateKeys(res, standardizedFDs);
        return candidateKeys;
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

    private String[] determineCandidateKeys(Integer[] essentialAttr, Integer[][][] functionalDependencies) {
        LinkedList<Integer> accessoryAttributes = new LinkedList<>(attributeMapping.values());
        accessoryAttributes.removeAll(Arrays.stream(essentialAttr).collect(Collectors.toList()));

        //Sort FDs based on length of keys (left side)
        Arrays.sort(functionalDependencies, Comparator.comparingInt(x -> x[0].length));
        //We'll use this a variable to use as our working candidate key list
        LinkedList<Integer> ckPlaceholder = new LinkedList<>(Arrays.stream(essentialAttr).collect(Collectors.toList()));

        LinkedList<Integer[]> candidateKeys = new LinkedList<>();
        //If the minimum, most essential attributes turns out to be our key, return. Adding new attributes is not minimal
        //and we can't remove any element from our essential attr list
        if (testCandidateKey(ckPlaceholder, functionalDependencies) == 0) {
            return convertCandidateKeys(new Integer[][]{essentialAttr});
        }

        while (ckPlaceholder.size() < attributeMapping.size()) {
            //Keep track of the accessory attribute with the most minimal heuristic
            //Default to the first accessoryHeuristic...
            Integer accessoryHeuristic = accessoryAttributes.getFirst();
            int minimalHeuristic = Integer.MAX_VALUE;
            for (Integer i : accessoryAttributes) {
                ckPlaceholder.addLast(i);
                int heuristic = testCandidateKey(ckPlaceholder, functionalDependencies);
                if (heuristic < minimalHeuristic) {
                    minimalHeuristic = heuristic;
                    accessoryHeuristic = i;
                }
                if (heuristic == 0) {
                    candidateKeys.add(ckPlaceholder.toArray(new Integer[candidateKeys.size()]));
                }
                ckPlaceholder.removeLast();
            }
            if (minimalHeuristic == 0) {
                break;
            } else {
                //Add the accessory attribute that's associated with the most minimal heuristic to the tail end of our
                //working candidate key list
                ckPlaceholder.addLast(accessoryHeuristic);
            }
        }
        return convertCandidateKeys(candidateKeys.toArray(new Integer[candidateKeys.size()][]));
    }

    /**
     * Tests whether a candidate key is valid
     *
     * @param currentCK
     * @param functionalDependencies
     * @return Amount of unmatched functional dependencies
     */
    private int testCandidateKey(List<Integer> currentCK, Integer[][][] functionalDependencies) {
        //ASSUMPTION: FD keys are sorted by length
        Set<Integer> ckAttrSet = new TreeSet<>();
        ckAttrSet.addAll(currentCK);

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
        return unmatched.size();
    }

    /**
     * Converts candidate keys from indicies to appropriate attribute names
     * @param candidateKeys
     * @return Collection of candidate keys
     */
    private String[] convertCandidateKeys(Integer[][] candidateKeys) {
        List<String> convertedRes = Arrays.stream(candidateKeys)
                .map(x -> {
                    StringBuilder tempCK = new StringBuilder();
                    for(Integer i: x){
                        tempCK.append(reversedAttrMapping.get(i));
                    }
                    return tempCK.toString();
                })
                .collect(Collectors.toList());
        return convertedRes.toArray(new String[convertedRes.size()]);
    }
}
