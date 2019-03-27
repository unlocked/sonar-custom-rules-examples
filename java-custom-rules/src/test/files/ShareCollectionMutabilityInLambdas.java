class ShareCollectionMutabilityInLambdas {
    private Map<Integer, Integer> sourceMinBodySizeMap = new HashMap<>();

    public void meth() {
        prop.entrySet().forEach(p -> {
            sourceMinBodySizeMap.put(Integer.valueOf((String)p.getKey()),
                    Integer.valueOf((String)p.getValue()));
        });
    }
}