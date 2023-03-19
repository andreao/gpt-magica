package no.volve;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardMathFormatterTest {

    @Test
    public void testFormatSimpleExpression() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("2", "+", "3"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("2 + 3", formatter.format(parsedExpression));
    }

    @Test
    public void testFormatExpressionWithParentheses() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("(", "2", "+", "3", ")", "*", "4"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("(2 + 3) * 4", formatter.format(parsedExpression));
    }

    @Test
    public void testFormatExpressionWithVariables() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("x", "^", "2", "-", "y", "^", "2"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("x ^ 2 - y ^ 2", formatter.format(parsedExpression));
    }


    @Test
    public void testFormatExpressionWithMinus() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("-", "x", "+", "y"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("-x + y", formatter.format(parsedExpression));
    }

    @Test
    public void testFormatMultipleArguments() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("1", "+", "2", "+", "3"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("1 + 2 + 3", formatter.format(parsedExpression));
    }

    @Test
    public void testFormatComplexExpression() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("(", "2", "+", "3", ")", "*", "(", "4", "-", "5", ")", "/", "(", "6", "^", "2", ")"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("(2 + 3) * (4 - 5) / 6 ^ 2", formatter.format(parsedExpression));
    }

    @Test
    public void testFormatExpressionWithFunction() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("sin", "(", "x", ")", "*", "cos", "(", "y", ")"));
        Node parsedExpression = parser.parse();
        StandardMathFormatter formatter = new StandardMathFormatter(parser);
        assertEquals("sin(x) * cos(y)", formatter.format(parsedExpression));
    }
}
