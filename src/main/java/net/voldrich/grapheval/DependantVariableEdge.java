package net.voldrich.grapheval;

import org.jgrapht.graph.DefaultEdge;

public class DependantVariableEdge extends DefaultEdge {
    private String varName;

    public DependantVariableEdge(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public String toString() {
        return varName;
    }
}
