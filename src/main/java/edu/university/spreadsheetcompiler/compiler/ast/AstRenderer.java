package edu.university.spreadsheetcompiler.compiler.ast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public final class AstRenderer {
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private AstRenderer() {}

    public static String toPrettyJson(AstNode node) {
        if (node == null) {
            return "null";
        }
        try {
            return MAPPER.writeValueAsString(node.toMap());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize AST to JSON", e);
        }
    }

    public static String toTree(AstNode node) {
        StringBuilder builder = new StringBuilder();
        render(node, builder, "", true);
        return builder.toString();
    }

    private static void render(AstNode node, StringBuilder out, String prefix, boolean isTail) {
        if (node == null) {
            out.append(prefix).append(isTail ? "└── " : "├── ").append("null").append('\n');
            return;
        }

        out.append(prefix).append(isTail ? "└── " : "├── ").append(label(node)).append('\n');
        String childPrefix = prefix + (isTail ? "    " : "│   ");

        if (node instanceof BinaryExpression binary) {
            render(binary.left(), out, childPrefix, false);
            render(binary.right(), out, childPrefix, true);
            return;
        }
        if (node instanceof FunctionCallNode functionCallNode) {
            for (int i = 0; i < functionCallNode.arguments().size(); i++) {
                render(functionCallNode.arguments().get(i), out, childPrefix, i == functionCallNode.arguments().size() - 1);
            }
            return;
        }
        if (node instanceof RangeNode rangeNode) {
            render(rangeNode.start(), out, childPrefix, false);
            render(rangeNode.end(), out, childPrefix, true);
        }
    }

    private static String label(AstNode node) {
        if (node instanceof NumberNode numberNode) return "NumberNode(" + numberNode.value() + ")";
        if (node instanceof CellNode cellNode) return "CellNode(" + cellNode.reference() + ")";
        if (node instanceof RangeNode) return "RangeNode";
        if (node instanceof FunctionCallNode functionCallNode) return "FunctionCallNode(" + functionCallNode.name() + ")";
        if (node instanceof BinaryExpression binary) return "BinaryExpression(" + binary.operator() + ")";
        return node.getClass().getSimpleName();
    }
}

