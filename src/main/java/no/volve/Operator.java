package no.volve;

public class Operator {
    public enum OperatorType {
        PREFIX, INFIX, POSTFIX
    }

    public enum Associativity {
        LEFT, RIGHT, BOTH
    }

    public final String functionName;
    public final int precedence;
    public final Associativity associativity;
    public final String operatorCharacter;
    public final OperatorType operatorType;

    public Operator(String functionName, int precedence, Associativity associativity, String operatorCharacter, OperatorType operatorType) {
        this.functionName = functionName;
        this.precedence = precedence;
        this.associativity = associativity;
        this.operatorCharacter = operatorCharacter;
        this.operatorType = operatorType;
    }
}
