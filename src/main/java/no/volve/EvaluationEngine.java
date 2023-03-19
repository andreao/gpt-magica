package no.volve;

import java.util.*;
import java.util.stream.Collectors;

public class EvaluationEngine {
    public enum Attribute {
        COMMUTATIVE
    }

    private final PatternMatcher patternMatcher;
    private final List<Rule> simplificationRules;
    private final Map<String, Set<Attribute>> functionAttributes;

    public EvaluationEngine(PatternMatcher patternMatcher, List<Rule> rules, Map<String, Set<Attribute>> functionAttributes) {
        this.patternMatcher = patternMatcher;
        this.simplificationRules = rules;
        this.functionAttributes = functionAttributes;
    }

    public Node evaluate(Node node) {
        boolean globalChanged;
        do {
            globalChanged = false;

            // Recursively call applyRules to arguments
            if (node instanceof FunctionNode) {
                FunctionNode functionNode = (FunctionNode) node;
                List<Node> newArguments = functionNode.arguments.stream()
                        .map(this::evaluate)
                        .collect(Collectors.toList());
                node = new FunctionNode(functionNode.name, flattenArguments(newArguments));
            }

            // Apply sorting if needed
            if (node instanceof FunctionNode) {
                FunctionNode functionNode = (FunctionNode) node;
                Set<Attribute> attributes = functionAttributes.get(functionNode.name);
                if (attributes != null && attributes.contains(Attribute.COMMUTATIVE)) {
                    node = new FunctionNode(functionNode.name, sortArguments(functionNode.arguments));
                }
            }

            // Run each rule in order
            for (Rule rule : simplificationRules) {
                Node newNode = applyRule(node, rule);
                if (newNode != null && !newNode.equals(node)) {
                    node = newNode;
                    globalChanged = true;
                    break;
                }
            }

        } while (globalChanged);

        return node;
    }

    private List<Node> flattenArguments(List<Node> arguments) {
        List<Node> flattenedArguments = new ArrayList<>();
        for (Node arg : arguments) {
            if (arg instanceof FunctionNode && "Sequence".equals(((FunctionNode) arg).name)) {
                flattenedArguments.addAll(flattenArguments(((FunctionNode) arg).arguments));
            } else {
                flattenedArguments.add(arg);
            }
        }
        return flattenedArguments;
    }


    private Node applyRule(Node node, Rule rule) {
        Map<String, Node> bindings = new HashMap<>();
        if (patternMatcher.match(rule.pattern, node, bindings)) {
            Node transformedNode = rule.transformer.transform(node, bindings);
            if (transformedNode != null) {
                return transformedNode;
            }
        }
        return node;
    }

    private List<Node> sortArguments(List<Node> arguments) {
        arguments.sort(new NodeComparator());
        return arguments;
    }

    private static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node n1, Node n2) {
            if (n1 instanceof IntegerNode && n2 instanceof IntegerNode) {
                return Integer.compare(((IntegerNode) n1).value, ((IntegerNode) n2).value);
            } else if (n1 instanceof RealNode && n2 instanceof RealNode) {
                return ((RealNode) n1).value.compareTo(((RealNode) n2).value);
            } else if (n1 instanceof VariableNode && n2 instanceof VariableNode) {
                return ((VariableNode) n1).name.compareTo(((VariableNode) n2).name);
            } else if (n1 instanceof FunctionNode && n2 instanceof FunctionNode) {
                FunctionNode fn1 = (FunctionNode) n1;
                FunctionNode fn2 = (FunctionNode) n2;

                int nameCompare = fn1.name.compareTo(fn2.name);
                if (nameCompare != 0) {
                    return nameCompare;
                } else {
                    int sizeCompare = Integer.compare(fn1.arguments.size(), fn2.arguments.size());
                    if (sizeCompare != 0) {
                        return sizeCompare;
                    }

                    for (int i = 0; i < fn1.arguments.size(); i++) {
                        int argCompare = compare(fn1.arguments.get(i), fn2.arguments.get(i));
                        if (argCompare != 0) {
                            return argCompare;
                        }
                    }
                }
            }
            // Add this line to fix the issue
            return Integer.compare(nodeTypeIndex(n1), nodeTypeIndex(n2));
        }

        private int nodeTypeIndex(Node node) {
            if (node instanceof IntegerNode) {
                return 0;
            } else if (node instanceof RealNode) {
                return 1;
            } else if (node instanceof VariableNode) {
                return 2;
            } else if (node instanceof FunctionNode) {
                return 3;
            }
            return -1;
        }
    }

}