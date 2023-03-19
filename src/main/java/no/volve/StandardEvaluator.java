package no.volve;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class StandardEvaluator {
    private EvaluationEngine evaluationEngine;
    private static final Map<String, Set<EvaluationEngine.Attribute>> FUNCTION_ATTRIBUTES = new HashMap<>();

    static {
        FUNCTION_ATTRIBUTES.put("Plus", EnumSet.of(EvaluationEngine.Attribute.COMMUTATIVE));
        FUNCTION_ATTRIBUTES.put("Times", EnumSet.of(EvaluationEngine.Attribute.COMMUTATIVE));
    }

    public StandardEvaluator() {
        PatternMatcher patternMatcher = new PatternMatcher();
        List<Rule> standardRules = Arrays.asList(
                new Rule(parse("Plus[Blank[x], Blank[y]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        Node x = bindings.get("x");
                        Node y = bindings.get("y");

                        if (!(x instanceof IntegerNode || x instanceof RealNode) || !(y instanceof IntegerNode || y instanceof RealNode)) {
                            return null;
                        }
                        if (x instanceof IntegerNode && y instanceof IntegerNode) {
                            return new IntegerNode(((IntegerNode) x).value + ((IntegerNode) y).value);
                        } else {
                            BigDecimal xValue = x instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) x).value) : ((RealNode) x).value;
                            BigDecimal yValue = y instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) y).value) : ((RealNode) y).value;

                            return new RealNode(xValue.add(yValue));
                        }
                    }
                }),
                new Rule(parse("Subtract[Blank[x], Blank[y]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        Node x = bindings.get("x");
                        Node y = bindings.get("y");

                        if (!(x instanceof IntegerNode || x instanceof RealNode) || !(y instanceof IntegerNode || y instanceof RealNode)) {
                            return null;
                        }
                        if (x instanceof IntegerNode && y instanceof IntegerNode) {
                            return new IntegerNode(((IntegerNode) x).value - ((IntegerNode) y).value);
                        } else {
                            BigDecimal xValue = x instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) x).value) : ((RealNode) x).value;
                            BigDecimal yValue = y instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) y).value) : ((RealNode) y).value;

                            return new RealNode(xValue.subtract(yValue));
                        }
                    }
                }),
                new Rule(parse("Times[Blank[x], Blank[y]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        Node x = bindings.get("x");
                        Node y = bindings.get("y");

                        if (!(x instanceof IntegerNode || x instanceof RealNode) || !(y instanceof IntegerNode || y instanceof RealNode)) {
                            return null;
                        }
                        if (x instanceof IntegerNode && y instanceof IntegerNode) {
                            return new IntegerNode(((IntegerNode) x).value * ((IntegerNode) y).value);
                        } else {
                            BigDecimal xValue = x instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) x).value) : ((RealNode) x).value;
                            BigDecimal yValue = y instanceof IntegerNode ? BigDecimal.valueOf(((IntegerNode) y).value) : ((RealNode) y).value;

                            return new RealNode(xValue.multiply(yValue));
                        }
                    }
                }),
                new Rule(parse("Divide[Blank[x], Blank[y]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        Node x = bindings.get("x");
                        Node y = bindings.get("y");

                        if (x instanceof IntegerNode && y instanceof IntegerNode) {
                            int xValue = ((IntegerNode) x).value;
                            int yValue = ((IntegerNode) y).value;
                            if (xValue % yValue == 0) {
                                return new IntegerNode(xValue / yValue);
                            }
                        }
                        return null;
                    }
                }),
                new Rule(parse("Power[Blank[x], Blank[y]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        Node x = bindings.get("x");
                        Node y = bindings.get("y");

                        if (x instanceof IntegerNode && y instanceof IntegerNode) {
                            int xValue = ((IntegerNode) x).value;
                            int yValue = ((IntegerNode) y).value;
                            int result = (int) Math.pow(xValue, yValue);

                            return new IntegerNode(result);
                        }

                        return null;
                    }
                }),
                new Rule(parse("Plus[BlankSequence[]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        FunctionNode functionNode = (FunctionNode) node;
                        List<Node> newArguments = functionNode.arguments.stream()
                                .filter(arg -> !(arg instanceof IntegerNode && ((IntegerNode) arg).value == 0))
                                .collect(Collectors.toList());

                        if (newArguments.size() == 0) {
                            return new IntegerNode(0);
                        } else if (newArguments.size() == 1) {
                            return newArguments.get(0);
                        } else {
                            return new FunctionNode(functionNode.name, newArguments);
                        }
                    }
                }),
                new Rule(parse("Times[BlankSequence[]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        FunctionNode functionNode = (FunctionNode) node;
                        List<Node> newArguments = functionNode.arguments.stream()
                                .filter(arg -> !(arg instanceof IntegerNode && ((IntegerNode) arg).value == 1))
                                .collect(Collectors.toList());

                        if (newArguments.size() == 0) {
                            return new IntegerNode(1);
                        } else if (newArguments.size() == 1) {
                            return newArguments.get(0);
                        } else {
                            return new FunctionNode(functionNode.name, newArguments);
                        }
                    }
                }),
                new Rule(parse("Plus[BlankSequence[]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        FunctionNode functionNode = (FunctionNode) node;
                        Set<Node> toRemove = new HashSet<>();
                        for (int i = 0; i < functionNode.arguments.size(); i++) {
                            Node arg1 = functionNode.arguments.get(i);
                            for (int j = i + 1; j < functionNode.arguments.size(); j++) {
                                Node arg2 = functionNode.arguments.get(j);

                                if (arg1 instanceof FunctionNode && ((FunctionNode) arg1).name.equals("Minus") &&
                                        ((FunctionNode) arg1).arguments.size() == 1 && ((FunctionNode) arg1).arguments.get(0).equals(arg2)) {
                                    toRemove.add(arg1);
                                    toRemove.add(arg2);
                                    break;
                                }

                                if (arg2 instanceof FunctionNode && ((FunctionNode) arg2).name.equals("Minus") &&
                                        ((FunctionNode) arg2).arguments.size() == 1 && ((FunctionNode) arg2).arguments.get(0).equals(arg1)) {
                                    toRemove.add(arg1);
                                    toRemove.add(arg2);
                                    break;
                                }
                            }
                        }
                        List<Node> newArguments = functionNode.arguments.stream()
                                .filter(arg -> !toRemove.contains(arg))
                                .collect(Collectors.toList());
                        return new FunctionNode(functionNode.name, newArguments);
                    }
                }),
                new Rule(parse("Plus[BlankSequence[]]"), new NodeTransformer() {
                    @Override
                    public Node transform(Node node, Map<String, Node> bindings) {
                        if (node instanceof FunctionNode) {
                            FunctionNode functionNode = (FunctionNode) node;
                            Map<Node, Integer> coefficients = new HashMap<>();
                            boolean changed = false;

                            for (Node arg : functionNode.arguments) {
                                if (arg instanceof FunctionNode && ((FunctionNode) arg).name.equals("Times")) {
                                    FunctionNode timesNode = (FunctionNode) arg;
                                    Node first = timesNode.arguments.get(0);
                                    Node second = timesNode.arguments.get(1);
                                    if (first instanceof IntegerNode && second instanceof VariableNode) {
                                        coefficients.put(second, coefficients.getOrDefault(second, 0) + ((IntegerNode) first).value);
                                        changed = true;
                                    }
                                } else if (arg instanceof VariableNode) {
                                    coefficients.put(arg, coefficients.getOrDefault(arg, 0) + 1);
                                    changed = changed || coefficients.get(arg) > 1;
                                }
                            }

                            if (changed) {
                                List<Node> newArgs = coefficients.entrySet().stream()
                                        .map(entry -> new FunctionNode("Times", Arrays.asList(new IntegerNode(entry.getValue()), entry.getKey())))
                                        .collect(Collectors.toList());

                                return new FunctionNode("Plus", newArgs);
                            }
                        }
                        return null;
                    }
                }),
                new Rule(parse("Subtract[Blank[x], 0]"), parse("x")),
                new Rule(parse("Subtract[Blank[x], Blank[y]]"), parse("Plus[x, Minus[y]]")),
                new Rule(parse("Plus[]"), parse("0")),
                new Rule(parse("Times[]"), parse("1")),
                new Rule(parse("Plus[Blank[x], Blank[x]]"), parse("Times[2, x]")),
                new Rule(parse("Times[1, Blank[x]]"), parse("x")),
                new Rule(parse("Plus[0, Blank[x]]"), parse("x")),
                new Rule(parse("Times[0, Blank[x]]"), parse("0")),
                new Rule(parse("Power[Blank[x], 0]"), parse("1")),
                new Rule(parse("Power[Blank[x], 1]"), parse("x")),
                new Rule(parse("Plus[Blank[x], Times[-1, Blank[x]]]"), parse("0")),
                new Rule(parse("Times[Blank[x], -1]"), parse("Times[-1, x]")),
                new Rule(parse("Times[-1, Times[-1, Blank[x]]]"), parse("x")),
                new Rule(parse("Plus[Blank[x], Minus[Blank[x]]]"), parse("0")),
                new Rule(parse("Times[Blank[x], Blank[x]]"), parse("Power[x, 2]"))
        );
        evaluationEngine = new EvaluationEngine(patternMatcher, standardRules, FUNCTION_ATTRIBUTES);
    }

    public Node evaluate(Node node) {
        return evaluationEngine.evaluate(node);
    }

    private Node parse(String input) {
        MathematicaParser parser = new MathematicaParser();
        return parser.parse(input);
    }
}