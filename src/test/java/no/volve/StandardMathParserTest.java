package no.volve;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardMathParserTest {

    @Test
    void testParseSimpleExpression() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("2", "*", "x", "+", "3"));
        Node expected = new FunctionNode("Plus", Arrays.asList(
                new FunctionNode("Times", Arrays.asList(
                        new IntegerNode(2),
                        new VariableNode("x"))),
                new IntegerNode(3)));
        assertEquals(expected, parser.parse());
    }

    @Test
    void testParseExpressionWithParentheses() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("(", "2", "+", "3", ")", "*", "x"));
        Node expected = new FunctionNode("Times", Arrays.asList(
                new FunctionNode("Plus", Arrays.asList(
                        new IntegerNode(2),
                        new IntegerNode(3))),
                new VariableNode("x")));
        assertEquals(expected, parser.parse());
    }

    @Test
    void testParseExpressionWithMultipleOperations() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("2", "*", "x", "+", "3", "/", "y"));
        Node expected = new FunctionNode("Plus", Arrays.asList(
                new FunctionNode("Times", Arrays.asList(
                        new IntegerNode(2),
                        new VariableNode("x"))),
                new FunctionNode("Divide", Arrays.asList(
                        new IntegerNode(3),
                        new VariableNode("y")))));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testParseExpressionWithMinus() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("-", "x", "+", "y"));
        Node expected = new FunctionNode("Plus", Arrays.asList(
                new FunctionNode("Minus", Arrays.asList(
                        new VariableNode("x"))),
                new VariableNode("y")));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testParseExpressionWithSubtract() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("x", "-", "y"));
        Node expected = new FunctionNode("Subtract", Arrays.asList(
                new VariableNode("x"),
                new VariableNode("y")));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testParseExpressionWithMultipleMinuses() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("-", "x", "-", "y"));
        Node expected = new FunctionNode("Subtract", Arrays.asList(
                new FunctionNode("Minus", Arrays.asList(
                        new VariableNode("x"))),
                new VariableNode("y")));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testParseExpressionWithMinusAndParentheses() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("(", "-", "x", "+", "y", ")", "*", "z"));
        Node expected = new FunctionNode("Times", Arrays.asList(
                new FunctionNode("Plus", Arrays.asList(
                        new FunctionNode("Minus", Arrays.asList(
                                new VariableNode("x"))),
                        new VariableNode("y"))),
                new VariableNode("z")));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testUnaryMinus() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("-", "x"));
        Node expected = new FunctionNode("Minus", Arrays.asList(new VariableNode("x")));
        assertEquals(expected, parser.parse());
    }

    @Test
    public void testParseMultipleArguments() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("1", "+", "2", "+", "3"));
        Node parsedExpression = parser.parse();
        FunctionNode expectedExpression = new FunctionNode("Plus", Arrays.asList(
                new FunctionNode("Plus", Arrays.asList(
                        new IntegerNode(1),
                        new IntegerNode(2))),
                new IntegerNode(3)));
        assertEquals(expectedExpression, parsedExpression);
    }

    @Test
    public void testParseSingleFunctionCall() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("sin", "(", "x", ")"));
        Node parsedExpression = parser.parse();
        assertEquals(new FunctionNode("sin", Collections.singletonList(new VariableNode("x"))), parsedExpression);
    }

    @Test
    public void testParseNestedFunctionCalls() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("sin", "(", "cos", "(", "x", ")", ")"));
        Node parsedExpression = parser.parse();
        assertEquals(new FunctionNode("sin", Collections.singletonList(new FunctionNode("cos", Collections.singletonList(new VariableNode("x"))))), parsedExpression);
    }

    @Test
    public void testParseExpressionWithFunction() {
        StandardMathParser parser = new StandardMathParser(Arrays.asList("sin", "(", "x", ")", "*", "cos", "(", "y", ")"));
        Node parsedExpression = parser.parse();
        Node expectedExpression = new FunctionNode("Times", Arrays.asList(
                new FunctionNode("sin", Collections.singletonList(new VariableNode("x"))),
                new FunctionNode("cos", Collections.singletonList(new VariableNode("y")))
        ));
        assertEquals(expectedExpression, parsedExpression);
    }
}