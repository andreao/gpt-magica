package no.volve;

import java.util.List;
import java.util.Objects;

class FunctionNode extends Node {
    public final String name;
    public final List<Node> arguments;

    public FunctionNode(String name, List<Node> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "FunctionNode{name='" + name + "', arguments=" + arguments + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FunctionNode that = (FunctionNode) obj;
        return Objects.equals(name, that.name) && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }
}
