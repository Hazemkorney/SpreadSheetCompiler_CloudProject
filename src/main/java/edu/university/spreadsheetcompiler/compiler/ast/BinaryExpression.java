package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.Map;

public final class BinaryExpression implements AstNode {
    private final String operator;
    private final AstNode left;
    private final AstNode right;

    public BinaryExpression(String operator, AstNode left, AstNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String operator() {
        return operator;
    }

    public AstNode left() {
        return left;
    }

    public AstNode right() {
        return right;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "type", "BinaryExpression",
                "operator", operator,
                "left", left.toMap(),
                "right", right.toMap()
        );
    }
}

