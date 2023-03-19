package no.volve;

import java.util.Objects;

class VariableNode extends Node {
    public final String name;

    VariableNode(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VariableNode other = (VariableNode) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "VariableNode{name='" + name + "'}";
    }
}
