package no.volve;

import java.util.ArrayList;
import java.util.List;

public class StandardMathTokenizer {
    private final String input;
    private int pos;

    public StandardMathTokenizer(String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<String> tokenize() {
        List<String> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char currentChar = input.charAt(pos);

            if (Character.isDigit(currentChar)) {
                tokens.add(parseNumber());
            } else if (Character.isLetter(currentChar)) {
                tokens.add(parseVariable());
            } else if (isOperator(currentChar)) {
                tokens.add(Character.toString(currentChar));
                pos++;
            } else if (currentChar == ' ') {
                pos++; // Ignore spaces
            } else {
                throw new IllegalArgumentException("Unexpected character: " + currentChar);
            }
        }

        return tokens;
    }

    private String parseNumber() {
        int startPos = pos;
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            pos++;
        }
        return input.substring(startPos, pos);
    }

    private String parseVariable() {
        StringBuilder variableBuilder = new StringBuilder();
        char currentChar = input.charAt(pos);

        if (Character.isLetter(currentChar)) {
            variableBuilder.append(currentChar);
            pos++;

            while (pos < input.length()) {
                currentChar = input.charAt(pos);
                if (Character.isLetterOrDigit(currentChar)) {
                    variableBuilder.append(currentChar);
                    pos++;
                } else {
                    break;
                }
            }
        }

        return variableBuilder.toString();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')';
    }
}