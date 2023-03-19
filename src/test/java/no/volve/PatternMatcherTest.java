package no.volve;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatternMatcherTest {
    private PatternMatcher patternMatcher;

    @BeforeEach
    void setUp() {
        patternMatcher = new PatternMatcher();
    }

    Node parse(String input) {
        Parser parser = new Parser(input);
        return parser.parse();
    }

    @Test
    void testEqualFunctionNodes() {
        Node pattern = parse("Plus[x, y]");
        Node candidate = parse("Plus[x, y]");

        assertTrue(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testUnequalFunctionNodes() {
        Node pattern = parse("Plus[x, y]");
        Node candidate = parse("Plus[y, x]");

        assertFalse(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testEqualNumberNodes() {
        Node pattern = parse("42");
        Node candidate = parse("42");

        assertTrue(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testUnequalNumberNodes() {
        Node pattern = parse("42");
        Node candidate = parse("24");

        assertFalse(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testEqualVariableNodes() {
        Node pattern = parse("x");
        Node candidate = parse("x");

        assertTrue(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testUnequalVariableNodes() {
        Node pattern = parse("x");
        Node candidate = parse("y");

        assertFalse(patternMatcher.match(pattern, candidate));
    }

    @Test
    void testBlankFunction() {
        Node pattern = parse("Blank[]");
        Node candidate1 = parse("x");
        Node candidate2 = parse("y");

        assertTrue(patternMatcher.match(pattern, candidate1));
        assertTrue(patternMatcher.match(pattern, candidate2));
    }

    @Test
    void testBlankFunctionWithSameVariable() {
        Node pattern = parse("Plus[Blank[x], Blank[x]]");
        Node candidate1 = parse("Plus[z, z]");
        Node candidate2 = parse("Plus[x, y]");

        assertTrue(patternMatcher.match(pattern, candidate1));
        assertFalse(patternMatcher.match(pattern, candidate2));
    }

    @Test
    void testBlankSequence() {
        Node pattern = parse("Function[BlankSequence[x]]");
        Node candidate1 = parse("Function[x, x]");
        Node candidate2 = parse("Function[x, y, x]");
        Node candidate3 = parse("Function[]");

        assertTrue(patternMatcher.match(pattern, candidate1));
        assertTrue(patternMatcher.match(pattern, candidate2));
        assertFalse(patternMatcher.match(pattern, candidate3));
    }

    @Test
    void testBlankNullSequence() {
        Node pattern = parse("Function[BlankNullSequence[x]]");
        Node candidate1 = parse("Function[x, x]");
        Node candidate2 = parse("Function[x, y, x]");
        Node candidate3 = parse("Function[]");

        assertTrue(patternMatcher.match(pattern, candidate1));
        assertTrue(patternMatcher.match(pattern, candidate2));
        assertTrue(patternMatcher.match(pattern, candidate3));
    }

}