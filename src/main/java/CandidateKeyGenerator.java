import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CandidateKeyGenerator {

    public static void main(String[] args) {
//        RelationSchema rs = new RelationSchema(new String[]{"A","B","C","D","E","F","G","H"});
//        rs.addFunctionalDependency(new String[]{"A"}, new String[]{"B","C"});
//        rs.addFunctionalDependency(new String[]{"B"}, new String[]{"C","F", "H"});
//        rs.addFunctionalDependency(new String[]{"C", "H"}, new String[]{"G"});
//        rs.addFunctionalDependency(new String[]{"E"}, new String[]{"A"});
//        rs.addFunctionalDependency(new String[]{"F"}, new String[]{"E","G"});
        RelationSchema rs = new RelationSchema(new String[]{"A", "B", "C", "D", "E", "F"});
        rs.addFunctionalDependency(new String[]{"A"}, new String[]{"B"});
        rs.addFunctionalDependency(new String[]{"B", "A"}, new String[]{"C"});
        rs.addFunctionalDependency(new String[]{"B", "C"}, new String[]{"A", "B"});
        rs.addFunctionalDependency(new String[]{"D", "E", "F"}, new String[]{"F", "E"});
        rs.addFunctionalDependency(new String[]{"D", "E", "C", "F"}, new String[]{"F"});
        rs.addFunctionalDependency(new String[]{"A", "B", "C", "D", "E", "F"}, new String[]{"F", "D"});
        String[] candidateKeys = CandidateKeyGenerator.generateCandidateKeys(rs);
        for(String ck: candidateKeys){
            System.out.println(ck);
        }
    }

    public static String[] generateCandidateKeys(RelationSchema schema) {
        if(schema.getAttributes() == null || schema.getAttributes().length == 0){
            return null;
        }
        Integer[] requiredAttributes = findEssentialAttributes(schema.getIntegrityConstraints(), schema.getAttributes().length);
        String[] candidateKeys = determineCandidateKeys(requiredAttributes, schema.getIntegrityConstraints(), schema.getAttributes());
        return candidateKeys;
    }

    /**
     * Determines the lower bound of the # of attributes necessary construct a candidate key
     * @param functionalDependencies
     * @return
     */
    private static Integer[] findEssentialAttributes(List<FunctionalDependency> functionalDependencies, int attributeQuantity) {
        //First we're going to simply determine whether the set of FDs contains all of the attr in the relation
        Set<Integer> attrSet = new HashSet<>(IntStream.range(0, attributeQuantity).boxed().collect(Collectors.toSet()));

        //Filter out trivial attributes that appear in the LHS and RHS
        List<FunctionalDependency> filteredFDs = functionalDependencies.stream()
                .map(x -> {
                    HashMap<Integer, Integer> countMap = new HashMap<>();
                    Stream.concat(
                            Arrays.stream(x.getStandardKeys()),
                            Arrays.stream(x.getStandardDerivations()))
                            .forEach(z -> {
                                countMap.computeIfPresent(z, (key, val) -> val + 1);
                                if(!countMap.containsKey(z)){
                                    countMap.put(z, 1);
                                }
                            });


                    List<Integer> filteredKeys = new ArrayList<>();
                    List<Integer> filteredDerivations = new ArrayList<>();
                    for(Integer key: x.getStandardKeys()){
                        if(countMap.containsKey(key) && countMap.get(key) == 1){
                            filteredKeys.add(key);
                        }
                    }
                    for(Integer deriv: x.getStandardDerivations()){
                        if(countMap.containsKey(deriv) && countMap.get(deriv) == 1){
                            filteredDerivations.add(deriv);
                        }
                    }
                    return new FunctionalDependency(filteredKeys.toArray(new Integer[0])
                            ,filteredDerivations.toArray(new Integer[0]));

                }).collect(Collectors.toList());

        Set<Integer> alphaSet = filteredFDs.stream()
                .map(FunctionalDependency::getStandardDerivations)
                .flatMap(Arrays::stream)
                .collect(Collectors.toSet());
        Set<Integer> primaryEssentialSet = new HashSet<>(attrSet);
        primaryEssentialSet.removeAll(alphaSet);

        List<Integer> mandatoryAttributes = new LinkedList<>(primaryEssentialSet);
        Integer[] finalRes = new Integer[mandatoryAttributes.size()];
        for(int i = 0; i < mandatoryAttributes.size(); i++){
            finalRes[i] = mandatoryAttributes.get(i);
        }
        return finalRes;
    }

    /**
     * Determines all possible candidate keys
     *
     * @param essentialAttributes
     * @param functionalDependencies
     * @param attributes
     * @return
     */
    private static String[] determineCandidateKeys(Integer[] essentialAttributes
            , List<FunctionalDependency> functionalDependencies
            , String[] attributes) {

        LinkedList<Integer> accessoryAttributes = new LinkedList<>(
                IntStream.range(0, attributes.length)
                        .boxed()
                        .collect(Collectors.toList())
        );

        accessoryAttributes.removeAll(Arrays.stream(essentialAttributes).collect(Collectors.toList()));

        //Sort FDs based on length of keys (left side)
        functionalDependencies.sort(Comparator.comparing(FunctionalDependency::getKeyLen));

        //We'll use this a variable to use as our working candidate key list
        LinkedList<Integer> ckPlaceholder = new LinkedList<>(Arrays.stream(essentialAttributes).collect(Collectors.toList()));

        LinkedList<Integer[]> candidateKeys = new LinkedList<>();
        //If the minimum, most essential attributes turns out to be our key, return. Adding new attributes is not minimal
        //and we can't remove any element from our essential attr list
        if (testCandidateKey(ckPlaceholder, functionalDependencies) == 0) {
            return convertCandidateKeys(new Integer[][]{essentialAttributes}, attributes);
        }

        while (ckPlaceholder.size() < attributes.length) {
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
                    candidateKeys.add(ckPlaceholder.toArray(new Integer[0]));
                }
                ckPlaceholder.removeLast();
            }
            if (minimalHeuristic == 0) {
                break;
            } else {
                //Add the accessory attribute that's associated with the most minimal heuristic to the tail end of our
                //working candidate key list
                ckPlaceholder.addLast(accessoryHeuristic);
                accessoryAttributes.remove(accessoryHeuristic);
            }
        }
        return convertCandidateKeys(candidateKeys.toArray(new Integer[0][]), attributes);
    }

    /**
     * Tests whether a candidate key is valid
     *
     * @param currentCK
     * @param functionalDependencies
     * @return Amount of unmatched functional dependencies
     */
    private static int testCandidateKey(List<Integer> currentCK, List<FunctionalDependency> functionalDependencies) {
        //ASSUMPTION: FD keys are sorted by length
        Set<Integer> ckAttrSet = new TreeSet<>(currentCK);

        List<Integer> unmatched = new LinkedList<>();
        for (int i = 0; i < functionalDependencies.size(); i++) {
            unmatched.add(i);
        }

        Integer priorSize;
        //If the prior unmatched FD size is the same as the unmatched FD size AFTER the loop, we break; This indicates
        //clearly that we cannot derive any more attributes from our given set of FDs
        do {
            priorSize = unmatched.size();

            for (Integer i : unmatched) {
                //Grab left side of FD
                Set<Integer> currentFD = new HashSet<>(
                        Arrays.stream(functionalDependencies.get(i).getStandardKeys())
                                .collect(Collectors.toSet()));
                //If our CK's length is less than the FD's length, we break. Since the FD array is sorted,
                //we can assume all proceeding FDs will be larger as well
                if(currentFD.size() > ckAttrSet.size()){
                    break;
                }
                currentFD.removeAll(ckAttrSet);
                //This would indicate that from our key set, we are able to derive another attribute; we
                //add it to our original set above
                if (currentFD.isEmpty()) {
                    ckAttrSet.addAll(Arrays.stream(functionalDependencies.get(i).getStandardDerivations())
                            .collect(Collectors.toSet()));
                    unmatched.remove(i);
                    //go back to the unmatched FDs whose size < ckKeySize, so see if anything changed
                    break;
                }
            }
        } while(!unmatched.isEmpty() && priorSize != unmatched.size());
        //Whether our attribute set is equivalent to our current derived CK set
        return unmatched.size();
    }

    /**
     * Converts candidate keys from indices to appropriate attribute names
     * @param candidateKeys
     * @return Collection of candidate keys
     */
    private static String[] convertCandidateKeys(Integer[][] candidateKeys, String[] attributes) {
        return Arrays.stream(candidateKeys)
                .map(x -> {
                    StringBuilder tempCK = new StringBuilder();
                    for(Integer i: x){
                        tempCK.append(attributes[i]);
                    }
                    return tempCK.toString();
                })
                .toArray(String[]::new);
    }
}
