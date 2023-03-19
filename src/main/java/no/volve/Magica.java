package no.volve;

import java.util.Scanner;

public class Magica {

    public static void main(String[] args) {
        StandardEvaluator evaluator = new StandardEvaluator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Magica - Advanced Calculator");
        System.out.println("Enter a mathematical expression or type 'quit' to exit:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }

            try {
                StandardMathTokenizer tokenizer = new StandardMathTokenizer(input);
                StandardMathParser parser = new StandardMathParser(tokenizer.tokenize());
                Node parsedExpression = parser.parse();
                Node evaluatedExpression = evaluator.evaluate(parsedExpression);
                StandardMathFormatter formatter = new StandardMathFormatter(parser);
                String formattedExpression = formatter.format(evaluatedExpression);
                System.out.println("Result: " + formattedExpression);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }
}