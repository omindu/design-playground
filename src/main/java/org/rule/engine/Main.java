package org.rule.engine;

import java.util.*;
import java.util.function.BiPredicate;

// Define operators using enum with BiPredicate logic for each comparison
enum Operator {
    EQUALS(Object::equals),
    NOT_EQUALS((a, b) -> !a.equals(b)),
    GREATER_THAN((a, b) -> ((Comparable<Object>) a).compareTo(b) > 0),
    LESS_THAN((a, b) -> ((Comparable<Object>) a).compareTo(b) < 0);

    private final BiPredicate<Object, Object> operation;

    Operator(BiPredicate<Object, Object> operation) {

        this.operation = operation;
    }

    public boolean apply(Object fieldValue, Object value) {

        return fieldValue != null && operation.test(fieldValue, value);
    }
}

// Base class for a single condition expression
final class Expression {

    private final String field;
    private final Operator operator;
    private final Object value;

    public Expression(String field, Operator operator, Object value) {

        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public boolean evaluate(Map<String, Object> context) {

        return operator.apply(context.get(field), value);
    }
}

// Logical condition types for composite rules
enum LogicalCondition {
    AND, OR
}

// Composite rule class that can hold multiple expressions and/or nested rules
final class CompositeRule {

    private final List<Expression> expressions;
    private final List<CompositeRule> nestedRules;
    private final LogicalCondition condition;

    public CompositeRule(LogicalCondition condition, List<Expression> expressions, List<CompositeRule> nestedRules) {

        this.condition = condition;
        this.expressions = expressions != null ? expressions : Collections.emptyList();
        this.nestedRules = nestedRules != null ? nestedRules : Collections.emptyList();
    }

    public boolean evaluate(Map<String, Object> context) {
        // Evaluate expressions and nested rules according to the logical condition
        boolean expressionsResult = expressions.isEmpty() ||
                (condition == LogicalCondition.AND
                        ? expressions.stream().allMatch(expr -> expr.evaluate(context))
                        : expressions.stream().anyMatch(expr -> expr.evaluate(context)));

        boolean nestedRulesResult = nestedRules.isEmpty() ||
                (condition == LogicalCondition.AND
                        ? nestedRules.stream().allMatch(rule -> rule.evaluate(context))
                        : nestedRules.stream().anyMatch(rule -> rule.evaluate(context)));

        return expressionsResult && nestedRulesResult;
    }
}

// Example usage
public class Main {

    public static void main(String[] args) {
        // Define expressions
        Expression expr1 = new Expression("application", Operator.EQUALS, "app1");
        Expression expr2 = new Expression("grantType", Operator.EQUALS, "password");
        Expression expr3 = new Expression("age", Operator.GREATER_THAN, 18);

        // Define a composite rule with expressions
        CompositeRule mainRule = new CompositeRule(LogicalCondition.AND,
                Arrays.asList(expr1, expr2, expr3), Collections.emptyList());

        // Define context
        Map<String, Object> context = Map.of(
                "application", "app1",
                "grantType", "password",
                "age", 20
                                            );

        // Evaluate the rule
        boolean result = mainRule.evaluate(context);
        System.out.println("Rule evaluation result: " + result);
    }
}