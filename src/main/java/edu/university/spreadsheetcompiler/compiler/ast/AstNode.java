package edu.university.spreadsheetcompiler.compiler.ast;

import java.util.Map;

public interface AstNode {
    Map<String, Object> toMap();
}

