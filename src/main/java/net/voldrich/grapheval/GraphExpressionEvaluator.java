package net.voldrich.grapheval;

import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphExpressionEvaluator {

    public static List<ExpressionWrapper> fromString(String ... expressions) {
        return Arrays.stream(expressions).map(str -> {
            String[] split = str.split("=");
            if (split.length != 2) {
                throw new RuntimeException("equation with single = expected");
            }
            return new ExpressionWrapper(split[0].trim(), split[1].trim());
        }).toList();
    }
    public static List<ExpressionWrapper> evaluate(List<ExpressionWrapper> expressionList) {
        if (expressionList.stream().allMatch(ExpressionWrapper::isEvaluated)) {
            return expressionList;
        }

        var directedGraph = buildDependencyGraph(expressionList);

        var cycleDetector = new CycleDetector<>(directedGraph);
        if (cycleDetector.detectCycles()) {
            throw new RuntimeException("Cycle detected");
        }

        final Map<String, Object> valueMap = expressionList.stream().filter(ExpressionWrapper::isEvaluated)
                .collect(Collectors.toMap(ExpressionWrapper::getName, exp -> (Object) exp.getValue()));
        var dfsIterator = new DepthFirstIterator<>(directedGraph);
        while (dfsIterator.hasNext()) {
            var expression = dfsIterator.next();
            if (!expression.isEvaluated()) {
                valueMap.put(expression.getName(), expression.evaluate(valueMap));
            }
        }

        return expressionList;
    }

    public static DefaultDirectedGraph<ExpressionWrapper, DefaultEdge> buildDependencyGraph(List<ExpressionWrapper> expressionList) {
        final Map<String, ExpressionWrapper> expressionNameMap = expressionList.stream()
                .collect(Collectors.toMap(ExpressionWrapper::getName, Function.identity()));

        var directedGraph = new DefaultDirectedGraph<ExpressionWrapper, DefaultEdge>(DefaultEdge.class);
        expressionList.forEach(directedGraph::addVertex);
        expressionList.forEach(expression -> {
            if (!expression.getVariables().isEmpty()) {
                expression.getVariables().forEach(varName -> {
                    var targetVertex = expressionNameMap.get(varName);
                    if (targetVertex == null) {
                        throw new RuntimeException("Unresolvable variable " + varName);
                    }
                    directedGraph.addEdge(expression, targetVertex);
                });
            }
        });
        return directedGraph;
    }
}
