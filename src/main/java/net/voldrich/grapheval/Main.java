package net.voldrich.grapheval;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import org.jgrapht.ext.JGraphXAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        evaluateAndPrint();
        evaluateAndDrawGraph();
    }

    private static void evaluateAndPrint() {
        List<ExpressionWrapper> expressions = GraphExpressionEvaluator.fromString(
                "a=b+1",
                "b=3",
                "c= b * a * 2",
                "d=2+1",
                "e=d*3"
        );

        GraphExpressionEvaluator.evaluate(expressions);

        System.out.println("Results:");
        DecimalFormat df = new DecimalFormat("#.##");
        expressions.forEach(exp -> System.out.printf("%s = %s %n", exp.getName(), df.format(exp.getValue())));
    }

    private static void evaluateAndDrawGraph() throws IOException {
        List<ExpressionWrapper> expressions = GraphExpressionEvaluator.fromString(
                "a=b+1",
                "b=3",
                "c= b * a * 2",
                "d=2+1",
                "e=d*3",
                "f=d+a"
        );
        var graph = GraphExpressionEvaluator.buildDependencyGraph(expressions);
        var graphAdapter = new JGraphXAdapter<>(graph);
        mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =  mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File("graph.png");
        ImageIO.write(image, "PNG", imgFile);
    }


}