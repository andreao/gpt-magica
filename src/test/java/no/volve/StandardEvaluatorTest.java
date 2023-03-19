package no.volve;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StandardEvaluatorTest {
    private final StandardEvaluator evaluator = new StandardEvaluator();

    private Node parse(String input) {
        MathematicaParser parser = new MathematicaParser();
        return parser.parse(input);
    }

    @Test
    public void testPlusSameVariable() {
        Node input = parse("Plus[x, x]");
        Node expected = parse("Times[2, x]");
        Node result = evaluator.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testTimesOne() {
        Node input = parse("Times[1, x]");
        Node expected = parse("x");
        Node result = evaluator.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    public void testPlusZero() {
        Node input = parse("Plus[0, x]");
        Node expected = parse("x");
        Node result = evaluator.evaluate(input);
        assertEquals(expected, result);
    }

    @Test
    void testTimesZero() {
        Node input = parse("Times[0, x]");
        Node expected = parse("0");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPowerZero() {
        Node input = parse("Power[x, 0]");
        Node expected = parse("1");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPowerOne() {
        Node input = parse("Power[x, 1]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testAddInverse() {
        Node input = parse("Plus[x, Times[-1, x]]");
        Node expected = parse("0");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTimesNegativeOne() {
        Node input = parse("Times[x, -1]");
        Node expected = parse("Times[-1, x]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testDoubleNegative() {
        Node input = parse("Times[-1, Times[-1, x]]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPlusInteger() {
        Node node = evaluator.evaluate(parse("Plus[1, 2]"));
        assertEquals(new IntegerNode(3), node);
    }

    @Test
    void testTimesReal() {
        Node node = evaluator.evaluate(parse("Times[1.5, 2.5]"));
        assertEquals(new RealNode(new BigDecimal("3.75")), node);
    }

    @Test
    void testPlusRealAndInteger() {
        Node node = evaluator.evaluate(parse("Plus[2.5, 2]"));
        assertEquals(new RealNode(new BigDecimal("4.5")), node);
    }

    @Test
    void testTimesRealAndInteger() {
        Node node = evaluator.evaluate(parse("Times[2.5, 2]"));
        assertEquals(new RealNode(new BigDecimal("5.0")), node);
    }

    @Test
    void testPlusReal() {
        Node node = evaluator.evaluate(parse("Plus[1.5, 2.5]"));
        assertEquals(new RealNode(new BigDecimal("4.0")), node);
    }

    @Test
    void testTimesInteger() {
        Node node = evaluator.evaluate(parse("Times[2, 3]"));
        assertEquals(new IntegerNode(6), node);
    }

    @Test
    void testNestedExpressions() {
        Node input = parse("Plus[Times[2, Plus[1, 1]], Times[3, 4]]");
        Node expected = parse("16");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSubtractInteger() {
        Node node = evaluator.evaluate(parse("Subtract[4, 2]"));
        assertEquals(new IntegerNode(2), node);
    }

    @Test
    void testSubtractRealAndInteger() {
        Node node = evaluator.evaluate(parse("Subtract[5.5, 2]"));
        assertEquals(new RealNode(new BigDecimal("3.5")), node);
    }

    @Test
    void testSubtractIntegerAndReal() {
        Node node = evaluator.evaluate(parse("Subtract[7, 2.5]"));
        assertEquals(new RealNode(new BigDecimal("4.5")), node);
    }

    @Test
    void testSubtractReal() {
        Node node = evaluator.evaluate(parse("Subtract[6.5, 2.5]"));
        assertEquals(new RealNode(new BigDecimal("4.0")), node);
    }

    @Test
    void testDivideInteger() {
        Node node = evaluator.evaluate(parse("Divide[8, 4]"));
        assertEquals(new IntegerNode(2), node);
    }

    @Test
    void testSubtractSameVariable() {
        Node node = evaluator.evaluate(parse("Subtract[x, x]"));
        assertEquals(new IntegerNode(0), node);
    }

    @Test
    void testTimesSameVariable() {
        Node node = evaluator.evaluate(parse("Times[x, x]"));
        assertEquals(parse("Power[x, 2]"), node);
    }

    @Test
    void testTimesPlusSimplification() {
        Node input = parse("Times[2, Plus[1, 1]]");
        Node expected = parse("4");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testNestedExpressionsSimplification() {
        Node input = parse("Plus[Times[2, Plus[1, 1]], Times[3, 4]]");
        Node expected = parse("16");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPowerInteger() {
        Node input = parse("Power[2, 3]");
        Node expected = parse("8");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testCommutativePlus() {
        Node input = parse("Plus[0, x]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testCommutativeTimes() {
        Node input = parse("Times[1, x]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingIntegersAndRealNumbers() {
        Node input = parse("Plus[2, 1.5, 1]");
        Node expected = parse("Plus[1, 2, 1.5]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingVariables() {
        Node input = parse("Plus[x, y, z]");
        Node expected = parse("Plus[x, y, z]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingFunctionsWithDifferentArgumentSizes() {
        Node input = parse("Plus[f[x, y], f[x], f[x, y, z]]");
        Node expected = parse("Plus[f[x], f[x, y], f[x, y, z]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingFunctionsWithSameArgumentSizes() {
        Node input = parse("Plus[f[x, y], f[y, z], f[x, z]]");
        Node expected = parse("Plus[f[x, y], f[x, z], f[y, z]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingFunctionsWithDifferentNames() {
        Node input = parse("Plus[g[x], f[x]]");
        Node expected = parse("Plus[f[x], g[x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingMixedTypes() {
        Node input = parse("Plus[2, x, f[x], 1.5, y, 1]");
        Node expected = parse("Plus[1, 2, 1.5, x, y, f[x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testCommutativePlusArgumentSorting() {
        Node input = parse("Plus[2, x, f[x], 1.5, f[f[x]], g[x], f[g[x]], f[x, y], 1]");
        Node expected = parse("Plus[1, 2, 1.5, x, f[x], f[f[x]], f[g[x]], f[x, y], g[x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingNestedFunctions() {
        Node input = parse("Plus[f[f[x]], g[x], f[x]]");
        Node expected = parse("Plus[f[x], f[f[x]], g[x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingIntegersRealNumbersAndVariables() {
        Node input = parse("Plus[2, x, 1.5, y, 1]");
        Node expected = parse("Plus[1, 2, 1.5, x, y]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSortingVariablesAndFunctions() {
        Node input = parse("Plus[x, f[x], g[x], y]");
        Node expected = parse("Plus[x, y, f[x], g[x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testSubtractZero() {
        Node input = parse("Subtract[x, 0]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPlusMinusSameVariable() {
        Node input = parse("Plus[x, y, Minus[x]]");
        Node expected = parse("y");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPlusTimes() {
        Node input = parse("Plus[x, Times[2, x]]");
        Node expected = parse("Times[3, x]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTimesMinus() {
        Node input = parse("Plus[Times[2, x], Minus[x]]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTimesPlusWithParentheses() {
        Node input = parse("Times[2, Plus[x, 1]]");
        Node expected = parse("Times[2, Plus[1, x]]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTimesVariable() {
        Node input = parse("Times[2, x]");
        Node expected = parse("Times[2, x]");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPlusWithBlankSequence() {
        Node input = parse("Plus[x, 0]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Plus[0, x]");
        expected = parse("x");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Plus[x, x, 0]");
        expected = parse("Times[2, x]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Plus[x, 0, x]");
        expected = parse("Times[2, x]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Plus[0, x, 0, x]");
        expected = parse("Times[2, x]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

    @Test
    void testTimesWithBlankSequence() {
        Node input = parse("Times[x, 1]");
        Node expected = parse("x");
        Node actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Times[1, x]");
        expected = parse("x");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Times[x, x, 1]");
        expected = parse("Power[x, 2]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Times[x, 1, x]");
        expected = parse("Power[x, 2]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);

        input = parse("Times[1, x, 1, x]");
        expected = parse("Power[x, 2]");
        actual = evaluator.evaluate(input);
        assertEquals(expected, actual);
    }

}