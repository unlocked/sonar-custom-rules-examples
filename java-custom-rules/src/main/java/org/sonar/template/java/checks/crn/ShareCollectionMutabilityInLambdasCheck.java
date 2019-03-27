package org.sonar.template.java.checks.crn;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.LambdaExpressionTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(
        key = "ShareCollectionMutabilityInLambdasCheck",
        name = "Shared Collection Mutability in Lambdas Should Be Avoided",
        description = "This lambda refers to non-local mutable collection or map. " +
                "Mutability is ok, sharing is nice, shared mutability is a bad practice. " +
                "Consider exploiting Lambdas API to eliminate this side-effect as in the example.",
        priority = Priority.BLOCKER,
        tags = "severe"
)
public class ShareCollectionMutabilityInLambdasCheck extends IssuableSubscriptionVisitor {
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return ImmutableList.of(Tree.Kind.LAMBDA_EXPRESSION);
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
                    ExpressionTree expression = expressionTree.expression();
                    if (expression.is(Tree.Kind.METHOD_INVOCATION)) {
                        checkLambdaInvocation((MethodInvocationTree) expression);
                    }

                }
            }
        } else if (lambdaBody.is(Tree.Kind.METHOD_INVOCATION)) {
            checkLambdaInvocation((MethodInvocationTree) lambdaBody);
        }
    }

    private void checkLambdaInvocation(MethodInvocationTree childTree) {
        MemberSelectExpressionTree methodSelect
                = (MemberSelectExpressionTree) childTree.methodSelect();
        ExpressionTree expression = methodSelect.expression();
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            IdentifierTree identifierTree = (IdentifierTree) expression;
            if (identifierTree.symbol().type().isSubtypeOf("java.util.Collection")
                    || identifierTree.symbol().type().isSubtypeOf("java.util.Map")) {
                reportIssue(identifierTree, "Collection or Map \""
                        + identifierTree.symbol().name() + "\" possibly sharing it's mutability.");
            }
        }
    }
}
