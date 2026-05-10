# Spreadsheet Formula Grammar (EBNF)

```ebnf
formula         = ["="], expression ;

expression      = comparison ;
comparison      = term, { ( ">" | "<" | ">=" | "<=" ), term } ;
term            = factor, { ( "+" | "-" ), factor } ;
factor          = unary, { ( "*" | "/" ), unary } ;
unary           = [ "-" ], primary ;

primary         = number
                | cell_ref, [ ":", cell_ref ]
                | function_call
                | "(", expression, ")" ;

function_call   = function_name, "(", [ arguments ], ")" ;
arguments       = expression, { ",", expression } ;

function_name   = "SUM" | "IF" | "MAX" | "MIN" ;
number          = digit, { digit } ;
cell_ref        = letters, non_zero_digit, { digit } ;

letters         = letter, { letter } ;
digit           = "0" | non_zero_digit ;
non_zero_digit  = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
letter          = "A" | ... | "Z" ;
```

## Function Validation Rules

- `IF` must have exactly 3 arguments.
- `SUM` must have one argument or more.

