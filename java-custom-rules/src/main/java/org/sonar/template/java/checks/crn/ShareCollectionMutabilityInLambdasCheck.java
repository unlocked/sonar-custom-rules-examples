package org.sonar.template.java.checks.crn;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.java.JavaVersionAwareVisitor;
import org.sonar.java.model.JavaTree;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaVersion;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.LambdaExpressionTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.ParameterizedTypeTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule()
public class ShareCollectionMutabilityInLambdasCheck extends IssuableSubscriptionVisitor
        implements JavaVersionAwareVisitor {
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.LAMBDA_EXPRESSION);
    }

    @Override
    public boolean isCompatibleWithJavaVersion(JavaVersion version) {
        return version.isJava8Compatible();
    }

    @Override
    public void visitNode(Tree tree) {
        LambdaExpressionTree let = (LambdaExpressionTree) tree;
        Tree lambdaBody = let.body();
        if (lambdaBody.is(Tree.Kind.BLOCK)) {
            List<StatementTree> body = ((BlockTree) lambdaBody).body();
            for (StatementTree statement : body) {
                if (statement.is(Tree.Kind.EXPRESSION_STATEMENT)) {
                    ExpressionStatementTree expressionTree = (ExpressionStatementTree) statement;
                    for (Tree childTree : ((JavaTree)expressionTree).getChildren()) {
                        if (childTree.is(Tree.Kind.METHOD_INVOCATION)) {
                            MethodInvocationTree methodInvocationTree = (MethodInvocationTree) childTree;
                            MemberSelectExpressionTree methodSelect = (MemberSelectExpressionTree) methodInvocationTree.methodSelect();
                            IdentifierTree identifierTree = (IdentifierTree) methodSelect.expression();
                            Tree declaration = identifierTree.symbol().declaration();
                            VariableTree variable = (VariableTree) declaration;
                            ParameterizedTypeTree type = (ParameterizedTypeTree) variable.type();
                            IdentifierTree typeIdentifier = (IdentifierTree) type.type();
                            typeIdentifier.symbol();
//                            if (type.type().isSubtypeOf("java.util.Collection")
//                                    || type.type().isSubtypeOf("java.util.Map")) {
//                                reportIssue(identifierTree, "Collection or Map \"" + variable.simpleName()
//                                        + "\" possibly sharing it's mutability.");
//                            }
                        }
                    }
                }
            }
        }
    }
}
