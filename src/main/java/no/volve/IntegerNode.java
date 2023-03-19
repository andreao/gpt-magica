package no.volve;

import java.util.Objects;

class IntegerNode extends Node {

    public final int value;

    IntegerNode(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerNode that = (IntegerNode) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "IntegerNode{" +
                "value=" + value +
                '}';
    }
}
