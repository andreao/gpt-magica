package no.volve;

import java.util.stream.Collectors;

public class StandardMathFormatter {

    private StandardMathParser parser;

    public StandardMathFormatter(StandardMathParser parser) {
        this.parser = parser;
    }

    public String format(Node node) {
        if (node instanceof FunctionNode) {
            FunctionNode functionNode = (FunctionNode) node;
            Operator operator = parser.getOperatorByFunctionName(functionNode.name);
            if (operator != null) {
                return formatOperatorNode(functionNode, operator);
            } else {
                // Format other function nodes
                return functionNode.name + "(" + functionNode.arguments.stream().map(this::format).collect(Collectors.joining(", ")) + ")";
            }
        } else if (node instanceof VariableNode) {
            return ((VariableNode) node).name;
        } else if (node instanceof IntegerNode) {
            return Integer.toString(((IntegerNode) node).value);
        } else if (node instanceof RealNode) {
            return ((RealNode) node).value.toPlainString();
        }
        throw new IllegalStateException("Unknown node type: " + node.getClass().getName());
    }

    private String formatOperatorNode(FunctionNode functionNode, Operator operator) {
        Node leftNode = functionNode.arguments.get(0);
        Node rightNode = functionNode.arguments.size() > 1 ? functionNode.arguments.get(1) : null;

        String left = format(leftNode);

        if (rightNode != null) {
            String right = format(rightNode);

            if (leftNode instanceof FunctionNode) {
                FunctionNode leftFunctionNode = (FunctionNode) leftNode;
                Operator leftOperator = parser.getOperatorByFunctionName(leftFunctionNode.name);
                if (leftOperator != null && (leftOperator.precedence < operator.precedence
                        || (leftOperator.precedence == operator.precedence && operator.associativity == Operator.Associativity.RIGHT))) {
                    left = "(" + left + ")";
                }
            }

            if (rightNode instanceof FunctionNode) {
                FunctionNode rightFunctionNode = (FunctionNode) rightNode;
                Operator rightOperator = parser.getOperatorByFunctionName(rightFunctionNode.name);
                if (rightOperator != null && (rightOperator.precedence < operator.precedence
                        || (rightOperator.precedence == operator.precedence && operator.associativity == Operator.Associativity.LEFT))) {
                    right = "(" + right + ")";
                }
            }

            return left + " " + operator.operatorCharacter + " " + right;
        } else {
            return operator.operatorCharacter + left;
        }
    }
}