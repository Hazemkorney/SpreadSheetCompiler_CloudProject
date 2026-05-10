package edu.university.spreadsheetcompiler.compiler;

public enum TokenType {
    EQUALS,
    NUMBER,
    CELL_REF,
    FUNCTION,
    IDENTIFIER,
    COMMA,
    LPAREN,
    RPAREN,
    COLON,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    GT,
    LT,
    GTE,
    LTE,
    EOF
}
