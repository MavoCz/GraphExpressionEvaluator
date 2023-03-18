# GraphExpressionEvaluator

The GraphExpressionEvaluator class is a Java class that provides functionality for 
evaluating multiple expressions based on dependant variables, where variables can also be expressions. 
The class is located in the GraphExpressionEvaluator.java file in 
the src/main/java/net/voldrich/grapheval/ directory of the project. 
The usage of the class is showcased in the Main.java file in the same directory.

The GraphExpressionEvaluator class provides the following functionality:

- Parsing and evaluation of expressions using EvalEx library
- Support for variables that can be also expressions
- Incremental calculation based on variable dependancy graph
- Cycle detection
- Using JgraphT library for all graph operations (variable dependencies, cycle detection, leaf to root evaluation)

Simple code, heavy dependencies :).