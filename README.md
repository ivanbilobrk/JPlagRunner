Example invocation from Intellij IDEA scratch:

```java
POST http://localhost:8080/run
Content-Type: application/json

{
  "firstSubmission": "#include <stdio.h>\n\nint main() {\n    int a = 0, b = 1;\n\n    while (a <= 100) {\n        printf(\"%d \", a);\n        int next = a + b;\n        a = b;\n        b = next;\n    }\n\n    return 0;\n}",
  "secondSubmission": "#include <stdio.h>\n\nint fibonacci(int n) {\n    if (n <= 1) return n;\n    return fibonacci(n - 1) + fibonacci(n - 2);\n}\n\nint main() {\n    int i = 0;\n    while (1) {\n        int fib = fibonacci(i);\n        if (fib > 100) break;\n        printf(\"%d \", fib);\n        i++;\n    }\n\n    return 0;\n}",
  "language": "c"
}
```

Supported languages: text, java, c, cpp, python, typescript, javascript.
