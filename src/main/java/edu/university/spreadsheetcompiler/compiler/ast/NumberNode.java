package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.Map;

public final class NumberNode implements AstNode {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of("type", "NumberNode", "value", value);
    }
}

