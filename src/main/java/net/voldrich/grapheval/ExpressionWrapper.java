package net.voldrich.grapheval;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class ExpressionWrapper {
    private final String name;
    private final String expressionStr;
    private final Expression expression;
    private final Set<String> variables;
    private BigDecimal value = null;

    public ExpressionWrapper(String name, String expressionStr) {
        this.name = name;
        this.expression = new Expression(expressionStr);
        this.expressionStr = expressionStr;
        try {
            this.variables = this.expression.getUsedVariables();
            if (this.variables.isEmpty()) {
                value = this.expression.evaluate().getNumberValue();
            }
        } catch (ParseException | EvaluationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public String getExpressionStr() {
        return expressionStr;
    }

    public BigDecimal evaluate(Map<String, Object> valueMap) {
        try {
            var result = expression.withValues(valueMap).evaluate();
            this.value = result.getNumberValue();
        } catch (EvaluationException | ParseException e) {
            throw new RuntimeException(e);
        }
        return this.value;
    }

    public boolean isEvaluated() {
        return value != null;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", name, expressionStr);
    }
}
