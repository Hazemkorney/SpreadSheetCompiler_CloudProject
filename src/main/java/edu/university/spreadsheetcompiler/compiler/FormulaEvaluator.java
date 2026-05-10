package edu.university.spreadsheetcompiler.compiler;

import edu.university.spreadsheetcompiler.compiler.ast.AstNode;
import edu.university.spreadsheetcompiler.compiler.ast.BinaryExpression;
import edu.university.spreadsheetcompiler.compiler.ast.FunctionCallNode;
import edu.university.spreadsheetcompiler.compiler.ast.NumberNode;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class FormulaEvaluator {

    public double evaluate(AstNode node) {
        if (node instanceof NumberNode numberNode) {
            return numberNode.value();
        }

        if (node instanceof BinaryExpression binaryExpression) {
            double left = evaluate(binaryExpression.left());
            double right = evaluate(binaryExpression.right());
            return switch (binaryExpression.operator()) {
                case "+" -> left + right;
                case "-" -> left - right;
                case "*" -> left * right;
                case "/" -> {
                    if (right == 0.0) {
                        throw new IllegalArgumentException("Division by zero");
                    }
                    yield left / right;
                }
                case ">" -> left > right ? 1.0 : 0.0;
                case "<" -> left < right ? 1.0 : 0.0;
                case ">=" -> left >= right ? 1.0 : 0.0;
                case "<=" -> left <= right ? 1.0 : 0.0;
                default -> throw new IllegalArgumentException("Unsupported operator: " + binaryExpression.operator());
            };
        }

        if (node instanceof FunctionCallNode functionCallNode) {
            String functionName = functionCallNode.name().toUpperCase(Locale.ROOT);
            return switch (functionName) {
                case "SUM" -> functionCallNode.arguments().stream().mapToDouble(this::evaluate).sum();
                default -> throw new IllegalArgumentException("Unsupported function: " + functionCallNode.name());
            };
        }

        throw new IllegalArgumentException("Unsupported expression type for final result");
    }
}
