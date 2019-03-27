import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

class ShareCollectionMutabilityInLambdas {
    private Map<Integer, Integer> sourceMinBodySizeMap = new HashMap<>();

    public void method() {
        prop.entrySet().forEach(p -> {
            sourceMinBodySizeMap.put(Integer.valueOf((String)p.getKey()), Integer.valueOf((String)p.getValue())); // Noncompliant
        });
    }
}