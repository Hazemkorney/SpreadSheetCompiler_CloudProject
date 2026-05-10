# AST System Design

## Class Hierarchy

- `AstNode` (interface)
  - `BinaryExpression`
  - `NumberNode`
  - `CellNode`
  - `FunctionCallNode`
  - `RangeNode`

All nodes implement `toMap()` for JSON serialization.

## AST Build Source

`Parser` builds AST directly during recursive-descent parsing:

- `expression()` -> comparison AST
- `term()` -> additive AST
- `factor()` -> multiplicative AST
- `functionCall()` -> function node + argument nodes
- range syntax `A1:A5` -> `RangeNode(CellNode("A1"), CellNode("A5"))`

## Example Formula

`=SUM(A1, MAX(B1:B5), 10)`

### Tree Visualization

```text
└── FunctionCallNode(SUM)
    ├── CellNode(A1)
    ├── FunctionCallNode(MAX)
    │   └── RangeNode
    │       ├── CellNode(B1)
    │       └── CellNode(B5)
    └── NumberNode(10.0)
```

### JSON Representation

```json
{
  "type": "FunctionCallNode",
  "name": "SUM",
  "arguments": [
    {"type": "CellNode", "reference": "A1"},
    {
      "type": "FunctionCallNode",
      "name": "MAX",
      "arguments": [
        {
          "type": "RangeNode",
          "start": {"type": "CellNode", "reference": "B1"},
          "end": {"type": "CellNode", "reference": "B5"}
        }
      ]
    },
    {"type": "NumberNode", "value": 10.0}
  ]
}
```

