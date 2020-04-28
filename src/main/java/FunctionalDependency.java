public class FunctionalDependency {
    private Integer[] standardKeys;
    private Integer[] standardDerivations;

    public FunctionalDependency(Integer[] standardKeys, Integer[] standardDerivations){
        this.standardKeys = standardKeys;
        this.standardDerivations = standardDerivations;
    }

    public int getKeyLen(){
        return standardKeys.length;
    }

    public Integer[] getStandardKeys(){
        return standardKeys;
    }

    public Integer[] getStandardDerivations(){
        return standardDerivations;
    }
}
