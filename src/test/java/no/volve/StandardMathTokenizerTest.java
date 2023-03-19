package no.volve;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardMathTokenizerTest {
    @Test
    void testTokenizeBasicExpression() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("2 * x + 3 * y");
        List<String> expectedTokens = Arrays.asList("2", "*", "x", "+", "3", "*", "y");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithParens() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("(a + b) * (a - b)");
        List<String> expectedTokens = Arrays.asList("(", "a", "+", "b", ")", "*", "(", "a", "-", "b", ")");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithExponentiation() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("3 * x^2 + 4 * x + 5");
        List<String> expectedTokens = Arrays.asList("3", "*", "x", "^", "2", "+", "4", "*", "x", "+", "5");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithSpaces() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer(" 2   *x +  3*y ");
        List<String> expectedTokens = Arrays.asList("2", "*", "x", "+", "3", "*", "y");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithMultiLetterVariables() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("velocity * time");
        List<String> expectedTokens = Arrays.asList("velocity", "*", "time");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithNumbersAndVariablesMixed() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("2a + 3b");
        List<String> expectedTokens = Arrays.asList("2", "a", "+", "3", "b");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeEmptyExpression() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("");
        List<String> expectedTokens = Arrays.asList();
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeSingleNumber() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("42");
        List<String> expectedTokens = Arrays.asList("42");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeSingleVariable() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("x");
        List<String> expectedTokens = Arrays.asList("x");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithVariablesContainingNumbers() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("a3 * b5 + c10");
        List<String> expectedTokens = Arrays.asList("a3", "*", "b5", "+", "c10");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeSingleDecimal() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("4.2");
        List<String> expectedTokens = Arrays.asList("4.2");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }

    @Test
    void testTokenizeExpressionWithDecimal() {
        StandardMathTokenizer tokenizer = new StandardMathTokenizer("3.14 * x");
        List<String> expectedTokens = Arrays.asList("3.14", "*", "x");
        assertEquals(expectedTokens, tokenizer.tokenize());
    }
}