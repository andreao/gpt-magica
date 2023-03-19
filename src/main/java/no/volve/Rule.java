package no.volve;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rule {
    public final Node pattern;
    public final NodeTransformer transformer;

    public Rule(Node pattern, Node replacement) {
        this.pattern = pattern;
        this.transformer = new NodeTransformer() {
            @Override
            public Node transform(Node node, Map<String, Node> bindings) {
                return buildReplacementNode(replacement, bindings);
            }
        };
    }

    public Rule(Node pattern, NodeTransformer transformer) {
        this.pattern = pattern;
        this.transformer = transformer;
    }

    private Node buildReplacementNode(Node node, Map<String, Node> bindings) {
        if (node instanceof VariableNode) {
            VariableNode variableNode = (VariableNode) node;
            if (bindings.containsKey(variableNode.name)) {
                return bindings.get(variableNode.name);
            }
        }
        if (node instanceof FunctionNode) {
            FunctionNode functionNode = (FunctionNode) node;
            List<Node> newArguments = new ArrayList<>();
            for (Node arg : functionNode.arguments) {
                newArguments.add(buildReplacementNode(arg, bindings));
            }
            return new FunctionNode(functionNode.name, newArguments);
        }
        return node;
    }
}
