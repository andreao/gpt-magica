package no.volve;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MathematicaParser {

    public static void main(String[] args) {
        String input = "Plus[Times[2, x], Power[x,2]]";
        Parser parser = new Parser(input);
        Node ast = parser.parse();
        System.out.println(ast);
    }

    public Node parse(String input) {
        Parser parser = new Parser(input);
        return parser.parse();
    }
}

class Parser {
    private final String input;
    private int pos = 0;

    Parser(String input) {
        this.input = input;
    }

    Node parse() {
        return parseExpression();
    }

    private Node parseExpression() {
        char currentChar = input.charAt(pos);
        if (Character.isDigit(currentChar) || currentChar == '-' || currentChar == '+' || currentChar == '.') {
            return parseNumber();
        } else if (Character.isLetter(currentChar)) {
            return parseFunctionOrVariable();
        } else {
            throw new IllegalArgumentException("Invalid input format");
        }
    }

    private Node parseNumber() {
        boolean negative = false;
        if (input.charAt(pos) == '+' || input.charAt(pos) == '-') {
            negative = input.charAt(pos) == '-';
            pos++;
        }
        StringBuilder number = new StringBuilder();
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            number.append(input.charAt(pos));
            pos++;
        }
        if (number.length() == 0) {
            throw new IllegalArgumentException("Invalid input format");
        }
        if (number.toString().contains(".")) {
            BigDecimal value = new BigDecimal(number.toString());
            return negative ? new RealNode(value.negate()) : new RealNode(value);
        } else {
            int value = Integer.parseInt(number.toString());
            return negative ? new IntegerNode(-value) : new IntegerNode(value);
        }
    }

    private Node parseFunctionOrVariable() {
        StringBuilder identifier = new StringBuilder();
        while (pos < input.length() && (Character.isAlphabetic(input.charAt(pos)) || input.charAt(pos) == '_')) {
            identifier.append(input.charAt(pos));
            pos++;
        }

        if (pos >= input.length() || input.charAt(pos) != '[') {
            return new VariableNode(identifier.toString());
        }

        pos++; // Consume '['
        consumeWhitespace();
        List<Node> arguments = new ArrayList<>();
        while (pos < input.length() && input.charAt(pos) != ']') {
            arguments.add(parseExpression());
            consumeWhitespace();
            if (pos < input.length() && input.charAt(pos) == ',') {
                pos++; // Consume ','
                consumeWhitespace();
            }
        }

        if (pos >= input.length() || input.charAt(pos) != ']') {
            throw new IllegalArgumentException("Invalid input format");
        }

        pos++; // Consume ']'
        consumeWhitespace();
        return new FunctionNode(identifier.toString(), arguments);
    }

    private void consumeWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }

}

