package net.voldrich.grapheval;


import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class GraphExpressionEvaluatorTest {
    @Test
    void testEval() throws EvaluationException, ParseException {
        Expression expression = new Expression("1 + 2 / (4 * SQRT(4))");
        EvaluationValue result = expression.evaluate();
        System.out.println(result.getNumberValue()); // prints 1.25
    }

    @Test
    void testMultipleExpressionEval() {
        var expressionList = GraphExpressionEvaluator.evaluate(List.of(new ExpressionWrapper("a", "1"),
                new ExpressionWrapper("b", "2"),
                new ExpressionWrapper("a_c2", "3+3"),
                new ExpressionWrapper("mat_doors", "a * 30"),
                new ExpressionWrapper("g", "h + 1"),
                new ExpressionWrapper("h", "40"),
                new ExpressionWrapper("duration", "a + b + a_c2 + mat_doors")));

        DecimalFormat format = new DecimalFormat("##.00");
        expressionList.forEach(exp -> System.out.println(exp.getName() + " = " + format.format(exp.getValue())));

        var nameToExp = expressionList.stream().collect(Collectors.toMap(
                ExpressionWrapper::getName,
                ExpressionWrapper::getValue));

        assertThat(nameToExp.get("duration")).isEqualTo(new BigDecimal(39));
    }

    @Test
    void testMultipleExpressionEvalWithCycle() {
        var expressionWrapperList = List.of(new ExpressionWrapper("a", "1"),
                new ExpressionWrapper("b", "2"),
                new ExpressionWrapper("a_c2", "3+3"),
                new ExpressionWrapper("mat_doors", "a * 30"),
                new ExpressionWrapper("g", "h + 1"),
                new ExpressionWrapper("h", "i * 3"),
                new ExpressionWrapper("i", "g * 4"),
                new ExpressionWrapper("duration", "a + b + a_c2 + mat_doors"));

        assertThatThrownBy(() -> GraphExpressionEvaluator.evaluate(expressionWrapperList))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cycle detected");
    }

    @Test
    void testMultipleExpressionEvalWithUnresolvedVariable() {
        var expressionWrapperList = List.of(new ExpressionWrapper("a", "1"),
                new ExpressionWrapper("b", "2"),
                new ExpressionWrapper("c", "3+3"),
                new ExpressionWrapper("mat_doors", "a * 30"),
                new ExpressionWrapper("duration", "a + b + a_c2 + mat_doors"));

        assertThatThrownBy(() -> GraphExpressionEvaluator.evaluate(expressionWrapperList))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unresolvable variable a_c2");
    }
}