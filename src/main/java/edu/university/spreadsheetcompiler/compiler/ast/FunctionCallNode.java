package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.List;
import java.util.Map;

public final class FunctionCallNode implements AstNode {
    private final String name;
    private final List<AstNode> arguments;

    public FunctionCallNode(String name, List<AstNode> arguments) {
        this.name = name;
        this.arguments = List.copyOf(arguments);
    }

    public String name() {
        return name;
    }

    public List<AstNode> arguments() {
        return arguments;
    }

    @Override
    public Map<String, Object> toMap() {
        return Map.of(
                "type", "FunctionCallNode",
                "name", name,
                "arguments", arguments.stream().map(AstNode::toMap).toList()
        );
    }
}

