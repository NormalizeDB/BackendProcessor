import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CandidateKeyGenerator {


    public static void main(String[] args) {
        String[][] list = convertFunctionalDependencies(new String[]{"A", "B", "C"}, new String[][]{{"ACB", "B"}, {"C", "A"}});
        for(String[] entry: list){
            System.out.println(entry[0] + " "+ entry[1]);
        }
    }

    /**
     * Converts an list of functional dependencies from attribute names >> index values
     *
     * @param relation relational schema
     * @param functionalDependencies set of FDs
     * @return
     */
    private static String[][] convertFunctionalDependencies(String[] relation, String[][] functionalDependencies) {
        Map<String, Integer> attributeMapping = IntStream.range(0, relation.length)
                .mapToObj(index -> new Object[]{relation[index], index})
                .collect(Collectors.toMap(x -> String.valueOf(x[0]), y -> Integer.valueOf(String.valueOf(y[1]))));
        List<String[]> convertedArr = Arrays.stream(functionalDependencies).map(x -> {
            List<String> convertedFD = Arrays.stream(x).map(y -> {
                StringBuilder finalResult = new StringBuilder();
                char[] tempArr = y.toCharArray();
                //So here, we're going to convert traditional attributes into a concat of their indicies
                //in terms of their relational schema
                for(char attr: tempArr){
                    finalResult.append(attributeMapping.get(String.valueOf(attr)));
                }
                return finalResult.toString();
            }).collect(Collectors.toList());
            return convertedFD.toArray(new String[convertedFD.size()]);
        }).collect(Collectors.toList());
        return convertedArr.toArray(new String[convertedArr.size()][]);
    }

    private int[] findEssentialAttributes(String[] relation, String[][] functionalDependencies) {
        //TODO: Finish functionality
        return null;
    }
}
