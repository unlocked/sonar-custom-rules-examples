package org.sonar.template.java.checks.crn;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ShareCollectionMutabilityInLambdasTest {

    @Test
    public void testCollectionMutabilityInLambda() {
        JavaCheckVerifier.verify(
                "src/test/files/ShareCollectionMutabilityInLambdas_Confluence.java",
                new ShareCollectionMutabilityInLambdasCheck());

        JavaCheckVerifier.verify(
                "src/test/files/ShareCollectionMutabilityInLambdas_Real_Code.java",
                new ShareCollectionMutabilityInLambdasCheck());
    }
}
