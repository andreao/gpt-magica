package no.volve;

import java.math.BigDecimal;
import java.util.*;

public class StandardMathParser {
    private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList("sin", "cos", "tan", "log", "exp"));
    private static final List<Operator> OPERATORS = Arrays.asList(
        new Operator("Plus", 1, Operator.Associativity.LEFT, "+", Operator.OperatorType.INFIX),
        new Operator("Subtract", 1, Operator.Associativity.LEFT, "-", Operator.OperatorType.INFIX),
        new Operator("Times", 2, Operator.Associativity.LEFT, "*", Operator.OperatorType.INFIX),
        new Operator("Divide", 2, Operator.Associativity.LEFT, "/", Operator.OperatorType.INFIX),
        new Operator("Power", 3, Operator.Associativity.RIGHT, "^", Operator.OperatorType.INFIX),
        new Operator("Minus", 4, Operator.Associativity.RIGHT, "-", Operator.OperatorType.PREFIX)
    );

    private List<String> tokens;

    public StandardMathParser(List<String> tokens) {
        this.tokens = tokens;
    }

    public Node parse() {
        UnparsedNode rootNode = buildTree();
        return reduceTree(rootNode);
    }

    public static int getMaxPrecedence() {
        return OPERATORS.stream().mapToInt(operator -> operator.precedence).max().orElse(0);
    }

    private UnparsedNode buildTree() {
        Stack<UnparsedNode> stack = new Stack<>();
        stack.push(new UnparsedNode());
        boolean isFunctionCall = false;
        for (String token : tokens) {
            if (isOperator(token)) {
                Operator operator;
                if (isPrefixOperatorExpected(stack.peek())) {
                    operator = findPrefixOperator(token);
                } else {
                    operator = findInfixOperator(token);
                }

                if (operator != null) {
                    stack.peek().nodes.add(new OperatorNode(operator));
                } else {
                    throw new IllegalStateException("Unknown operator: " + token);
                }
            } else if (isOperand(token) || isVariable(token) || isFunction(token)) {
                if (isFunction(token)) {
                    isFunctionCall = true;
                }
                stack.peek().nodes.add(createNode(token));
            } else if (token.equals("(")) {
                if (isFunctionCall) {
                    isFunctionCall = false;
                    Node functionNode = stack.peek().nodes.remove(stack.peek().nodes.size() - 1);
                    UnparsedNode newNode = new UnparsedNode();
                    FunctionNode newFunctionNode = new FunctionNode(((VariableNode) functionNode).name, newNode.nodes);
                    stack.peek().nodes.add(newFunctionNode);
                    stack.push(newNode);
                } else {
                    UnparsedNode newNode = new UnparsedNode();
                    stack.peek().nodes.add(newNode);
                    stack.push(newNode);
                }
            } else if (token.equals(")")) {
                if (stack.size() <= 1) {
                    throw new IllegalStateException("Mismatched parentheses.");
                }
                stack.pop();
            } else {
                throw new IllegalStateException("Unknown token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Mismatched parentheses.");
        }

        return stack.pop();
    }

    private boolean isPrefixOperatorExpected(UnparsedNode node) {
        if (node.nodes.isEmpty()) {
            return true;
        }

        Node lastNode = node.nodes.get(node.nodes.size() - 1);
        return lastNode instanceof OperatorNode && ((OperatorNode) lastNode).operator.operatorType == Operator.OperatorType.INFIX;
    }

    private Operator findInfixOperator(String character) {
        return OPERATORS.stream()
                .filter(op -> op.operatorCharacter.equals(character) && op.operatorType == Operator.OperatorType.INFIX)
                .findFirst()
                .orElse(null);
    }

    private Operator findPrefixOperator(String character) {
        return OPERATORS.stream()
                .filter(op -> op.operatorCharacter.equals(character) && op.operatorType == Operator.OperatorType.PREFIX)
                .findFirst()
                .orElse(null);
    }

    private Node reduceTree(UnparsedNode tree) {
        for (int precedence = getMaxPrecedence(); precedence >= 0; precedence--) {
            int i = 0;
            while (i < tree.nodes.size()) {
                Node node = tree.nodes.get(i);
                if (node instanceof OperatorNode) {
                    OperatorNode operatorNode = (OperatorNode) node;
                    if (operatorNode.operator.precedence == precedence) {
                        if (operatorNode.operator.operatorType == Operator.OperatorType.INFIX) {
                            Node left = i > 0 ? tree.nodes.remove(i - 1) : null;
                            Node right = tree.nodes.remove(i);
                            FunctionNode functionNode = new FunctionNode(operatorNode.operator.functionName, Arrays.asList(left, right));
                            tree.nodes.set(i - 1, functionNode);
                            continue;
                        } else if (operatorNode.operator.operatorType == Operator.OperatorType.PREFIX) {
                            Node operand = tree.nodes.remove(i + 1);
                            FunctionNode functionNode = new FunctionNode(operatorNode.operator.functionName, Collections.singletonList(operand));
                            tree.nodes.set(i, functionNode);
                        }
                    }
                } else if (node instanceof UnparsedNode) {
                    tree.nodes.set(i, reduceTree((UnparsedNode) node));
                }
                i++;
            }
        }
        return tree.nodes.size() == 1 ? tree.nodes.get(0) : new FunctionNode("List", tree.nodes);
    }

    private static boolean isOperator(String token) {
        return OPERATORS.stream().anyMatch(op -> op.functionName.equals(token) || op.operatorCharacter.equals(token));
    }

    private boolean isOperand(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    private boolean isVariable(String token) {
        return token.matches("[a-zA-Z]+\\d*");
    }

    private boolean isFunction(String token) {
        return FUNCTIONS.contains(token);
    }

    private Node createNode(String token) {
        if (isOperand(token)) {
            if (token.contains(".")) {
                return new RealNode(new BigDecimal(token));
            } else {
                return new IntegerNode(Integer.parseInt(token));
            }
        } else if (isVariable(token)) {
            return new VariableNode(token);
        } else {
            throw new IllegalStateException("Unknown token type: " + token);
        }
    }

    public Operator getOperatorByFunctionName(String functionName) {
        for (Operator operator : OPERATORS) {
            if (operator.functionName.equals(functionName)) {
                return operator;
            }
        }
        return null;
    }

    public class UnparsedNode extends Node {
        public List<Node> nodes = new ArrayList<>();
        public UnparsedNode() {
        }
        UnparsedNode(List<Node> nodes) {
            this.nodes.addAll(nodes);
        }
    }

    public class OperatorNode extends Node {
        private final Operator operator;

        public OperatorNode(Operator operator) {
            this.operator = operator;
        }

        @Override
        public String toString() {
            return "OperatorNode{operator=" + operator + '}';
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            OperatorNode other = (OperatorNode) obj;
            return Objects.equals(operator, other.operator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operator);
        }
    }
}