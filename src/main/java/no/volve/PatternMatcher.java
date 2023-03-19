package no.volve;

import java.util.*;

public class PatternMatcher {

    public boolean match(Node pattern, Node candidate) {
        Map<String, Node> bindings = new HashMap<>();
        return match(pattern, candidate, bindings);
    }

    public boolean match(Node pattern, Node candidate, Map<String, Node> bindings) {
        if (pattern instanceof FunctionNode && candidate instanceof FunctionNode) {
            FunctionNode patternFunction = (FunctionNode) pattern;
            FunctionNode candidateFunction = (FunctionNode) candidate;

            if (patternFunction.name.equals(candidateFunction.name)) {
                return matchArguments(patternFunction.arguments, candidateFunction.arguments, 0, 0, bindings);
            }
        } else if (pattern instanceof IntegerNode && candidate instanceof IntegerNode) {
            return pattern.equals(candidate);
        } else if (pattern instanceof VariableNode && candidate instanceof VariableNode) {
            return pattern.equals(candidate);
        } else if (pattern instanceof FunctionNode) {
            FunctionNode patternFunction = (FunctionNode) pattern;

            if ("Blank".equals(patternFunction.name)) {
                return matchBlank(patternFunction, candidate, bindings);
            }
        }
        return false;
    }

    private boolean matchArguments(List<Node> patternArgs, List<Node> candidateArgs, int patternIndex, int candidateIndex, Map<String, Node> bindings) {
        if (patternIndex == patternArgs.size()) {
            return candidateIndex == candidateArgs.size();
        }

        Node patternArg = patternArgs.get(patternIndex);

        if (patternArg instanceof FunctionNode) {
            FunctionNode patternFunction = (FunctionNode) patternArg;

            if ("BlankSequence".equals(patternFunction.name) || "BlankNullSequence".equals(patternFunction.name)) {
                if (patternIndex == patternArgs.size() - 1) {
                    if ("BlankNullSequence".equals(patternFunction.name) || candidateIndex < candidateArgs.size()) {
                        if (patternFunction.arguments.size() == 1 && patternFunction.arguments.get(0) instanceof VariableNode) {
                            String varName = ((VariableNode) patternFunction.arguments.get(0)).name;
                            List<Node> sequenceNodes = new ArrayList<>(candidateArgs.subList(candidateIndex, candidateArgs.size()));
                            bindings.put(varName, new FunctionNode("Sequence", sequenceNodes));
                        }
                        return true;
                    }
                } else {
                    for (int i = candidateIndex; i <= candidateArgs.size(); i++) {
                        Map<String, Node> tempBindings = new HashMap<>(bindings);
                        if (matchArguments(patternArgs, candidateArgs, patternIndex + 1, i, tempBindings)) {
                            bindings.putAll(tempBindings);
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        if (candidateIndex < candidateArgs.size() && match(patternArg, candidateArgs.get(candidateIndex), bindings)) {
            return matchArguments(patternArgs, candidateArgs, patternIndex + 1, candidateIndex + 1, bindings);
        }

        return false;
    }

    private boolean matchBlank(FunctionNode pattern, Node candidate, Map<String, Node> bindings) {
        if (pattern.arguments.isEmpty()) {
            return true;
        } else {
            Node patternArg = pattern.arguments.get(0);
            if (patternArg instanceof VariableNode) {
                String varName = ((VariableNode) patternArg).name;
                if (bindings.containsKey(varName)) {
                    return bindings.get(varName).equals(candidate);
                } else {
                    bindings.put(varName, candidate);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matchBlankSequence(FunctionNode pattern, FunctionNode candidate, Map<String, Node> bindings) {
        if (pattern.arguments.size() != 1 || !(pattern.arguments.get(0) instanceof VariableNode)) {
            return false;
        }
        String varName = ((VariableNode) pattern.arguments.get(0)).name;

        List<Node> sequenceNodes = new ArrayList<>();
        for (Node arg : candidate.arguments) {
            sequenceNodes.add(arg);
        }
        bindings.put(varName, new FunctionNode("Sequence", sequenceNodes));
        return true;
    }

    private boolean matchBlankNullSequence(FunctionNode pattern, FunctionNode candidate, Map<String, Node> bindings) {
        if (pattern.arguments.size() != 1 || !(pattern.arguments.get(0) instanceof VariableNode)) {
            return false;
        }
        String varName = ((VariableNode) pattern.arguments.get(0)).name;

        List<Node> sequenceNodes = new ArrayList<>();
        for (Node arg : candidate.arguments) {
            sequenceNodes.add(arg);
        }
        bindings.put(varName, new FunctionNode("Sequence", sequenceNodes));
        return true;
    }
}