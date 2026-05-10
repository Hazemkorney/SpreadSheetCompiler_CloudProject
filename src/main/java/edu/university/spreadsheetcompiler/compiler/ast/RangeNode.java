package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.Map;

public final class RangeNode implements AstNode {
    private final CellNode start;
    private final CellNode end;

    public RangeNode(CellNode start, CellNode end) {
        this.start = start;
        this.end = end;
    }

    public CellNode start() {
        return start;
    }

    public CellNode end() {
        return end;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "type", "RangeNode",
                "start", start.toMap(),
                "end", end.toMap()
        );
    }
}

