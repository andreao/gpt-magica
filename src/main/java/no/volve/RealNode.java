package no.volve;

import java.math.BigDecimal;
import java.util.Objects;

public class RealNode extends Node {
    public final BigDecimal value;

    RealNode(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealNode realNode = (RealNode) o;
        return value.compareTo(realNode.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RealNode{value=" + value + '}';
    }
}