import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

class ShareCollectionMutabilityInLambdas {
    private Map<Integer, Integer> sourceMinBodySizeMap = new HashMap<>();

    private DependableNode<MethodDeclaration> method(List<String> exceptions,
                                                           List<ArgumentVariation> variations,
                                                           Integer[] arguments) {
        String testCode = generateTestCode(exceptions, variations, arguments);
        MethodDeclaration testMethod = JavaParser.parseBodyDeclaration(testCode).asMethodDeclaration();

        Set<ImportDeclaration> imports = new HashSet<>(unit.getCu().getImports());
        imports.add(Imports.JUNIT_TEST);
        imports.add(Imports.MOCKITO);
        imports.add(new ImportDeclaration("com.aurea.unittest.OptionalExceptionRule", false, false));
        imports.add(new ImportDeclaration(String.format("%s.%s",
                unit.getPackageName(), unit.getClassName()), true, true));

        Set<FieldDeclaration> fields = new HashSet<>();
        fields.add(JavaParser.parseBodyDeclaration(generateRuleCode()).asFieldDeclaration());
        variations.forEach(v -> v.getTypeOptions()
                .forEach(option -> fields.add(JavaParser.parseBodyDeclaration(option.getCode()).asFieldDeclaration()))); // Noncompliant

        TestDependency dependency = new TestDependency();
        dependency.setImports(imports);
        dependency.setFields(fields);
        return DependableNode.from(testMethod, dependency);
    }
}