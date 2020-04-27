public class FunctionalDependency {
    private String[] keyVals;
    private String[] derivations;

    public FunctionalDependency(String[] keyVals, String[] derivations){
        this.keyVals = keyVals;
        this.derivations = derivations;
    }

    public int getKeyLen(){
        return keyVals.length;
    }

    public String[] getKeyVals(){
        return keyVals;
    }

    public String[] getDerivations(){
        return derivations;
    }

}
