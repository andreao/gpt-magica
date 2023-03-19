package no.volve;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MathematicaParserTest {
    private MathematicaParser parser = new MathematicaParser();

    @Test
    public void testSimpleVariable() {
        String input = "x";
        Node ast = parser.parse(input);
        assertEquals("VariableNode{name='x'}", ast.toString());
    }

    @Test
    public void testSimpleFunction() {
        String input = "Sin[x]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Sin', arguments=[VariableNode{name='x'}]}", ast.toString());
    }

    @Test
    public void testSimpleNumber() {
        String input = "42";
        Node ast = parser.parse(input);
        assertEquals("IntegerNode{value=42}", ast.toString());
    }

    @Test
    public void testNestedFunctions() {
        String input = "Plus[Times[2,x], Power[x,2]]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Plus', arguments=[FunctionNode{name='Times', arguments=[IntegerNode{value=2}, VariableNode{name='x'}]}, FunctionNode{name='Power', arguments=[VariableNode{name='x'}, IntegerNode{value=2}]}]}", ast.toString());
    }

    @Test
    public void testNestedFunctionsWithWhitespace() {
        String input = "Plus[ Times[ 2 , x ] , Power[ x , 2 ] ]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Plus', arguments=[FunctionNode{name='Times', arguments=[IntegerNode{value=2}, VariableNode{name='x'}]}, FunctionNode{name='Power', arguments=[VariableNode{name='x'}, IntegerNode{value=2}]}]}", ast.toString());
    }

    @Test
    public void testSingleWhitespace() {
        String input = "Plus[x, y]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Plus', arguments=[VariableNode{name='x'}, VariableNode{name='y'}]}", ast.toString());
    }

    @Test
    public void testMultipleWhitespace() {
        String input = "Plus[   x   ,   y   ]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Plus', arguments=[VariableNode{name='x'}, VariableNode{name='y'}]}", ast.toString());
    }

    @Test
    public void testNewlineWhitespace() {
        String input = "Plus[\nx,\ny\n]";
        Node ast = parser.parse(input);
        assertEquals("FunctionNode{name='Plus', arguments=[VariableNode{name='x'}, VariableNode{name='y'}]}", ast.toString());
    }

    @Test
    public void testInvalidInput() {
        String input = "@Invalid$input";
        assertThrows(IllegalArgumentException.class, () -> parser.parse(input));
    }

    @Test
    void testParsePlusWithBlanks() {
        Node expected = parser.parse("Plus[Blank[x], Times[-1, Blank[x]]]");
        Node actual = parser.parse("Plus[Blank[x], Times[-1, Blank[x]]]");
        assertEquals(expected, actual);
    }

    @Test
    void testParseTimesWithBlanks() {
        Node expected = parser.parse("Times[Blank[x], Times[-1, Blank[x]]]");
        Node actual = parser.parse("Times[Blank[x], Times[-1, Blank[x]]]");
        assertEquals(expected, actual);
    }

    @Test
    void testParseTimesWithNegativeNumber() {
        Node expected = parser.parse("Times[-1, x]");
        Node actual = parser.parse("Times[-1, x]");
        assertEquals(expected, actual);
    }

    @Test
    void testParseTimesWithMinusSign() {
        Node expected = parser.parse("Times[x, -1]");
        Node actual = parser.parse("Times[x, -1]");
        assertEquals(expected, actual);
    }

    @Test
    void testParseDecimalNumber() {
        Node node = parser.parse("3.14");
        assertEquals(new RealNode(new BigDecimal("3.14")), node);
    }

    @Test
    void testParseNegativeDecimalNumber() {
        Node node = parser.parse("-3.14");
        assertEquals(new RealNode(new BigDecimal("-3.14")), node);
    }

    @Test
    void testParsePositiveDecimalNumberWithExplicitSign() {
        Node node = parser.parse("+3.14");
        assertEquals(new RealNode(new BigDecimal("3.14")), node);
    }

    @Test
    void testParseFunctionWithDecimalNumber() {
        Node expected = parser.parse("Times[3.14, x]");
        Node actual = new FunctionNode("Times", Arrays.asList(new RealNode(new BigDecimal("3.14")), new VariableNode("x")));
        assertEquals(expected, actual);
    }

    @Test
    void testParseMultiArgumentFunction() {
        Node expected = parser.parse("Plus[x, y, z]");
        Node actual = new FunctionNode("Plus", Arrays.asList(new VariableNode("x"), new VariableNode("y"), new VariableNode("z")));
        assertEquals(expected, actual);
    }
}