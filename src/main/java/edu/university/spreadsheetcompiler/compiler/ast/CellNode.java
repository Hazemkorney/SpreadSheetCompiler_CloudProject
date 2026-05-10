package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.Map;

public final class CellNode implements AstNode {
    private final String reference;

    public CellNode(String reference) {
        this.reference = reference;
    }

    public String reference() {
        return reference;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of("type", "CellNode", "reference", reference);
    }
}

