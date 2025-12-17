# RCompiler-2025: A Rust Compiler Implementation

> A complete Rust compiler written in Java, implementing lexical analysis, syntax parsing, semantic analysis, and LLVM IR code generation.

**Project**: Autumn Semester 2025 ACM Class Compiler Project  
**Language**: Java  
**Target Language**: Rust (subset)  
**Output**: LLVM Intermediate Representation (IR)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Building & Running](#building--running)
- [Usage](#usage)
- [Compilation Pipeline](#compilation-pipeline)
- [Supported Rust Features](#supported-rust-features)
- [Testing](#testing)

---

## Overview

RCompiler-2025 is a compiler for a subset of the Rust programming language. It performs a complete compilation pipeline from source code to LLVM Intermediate Representation, which can then be compiled to machine code using standard LLVM tools like `clang`.

The compiler is designed to be modular and educational, with clear separation between different compilation phases:
- **Lexical Analysis** (Lexer)
- **Syntax Analysis** (Parser)
- **Semantic Analysis** (Semantics)
- **Code Generation** (IR)

---

## Features

### Language Support

- **Functions**: Function definitions with parameters and return types
- **Variables**: Mutable and immutable variable bindings
- **Primitive Types**: `i32`, `u32`, `isize`, `usize`, `bool`, `str`, `String`, `char`
- **Complex Types**: Structs, Enums, Arrays, Pointers, References
- **Control Flow**: `if/else`, `while`, `loop`expressions
- **Operators**: Binary, unary, comparison, logical operators
- **Traits & Impl**: Trait definitions and implementations
- **Constants**: Const item declarations
- **Blocks**: Expression blocks with implicit return values

### Compiler Features

- ✅ Full position tracking (line/column information for error reporting)
- ✅ Comprehensive error diagnostics
- ✅ Scope-based symbol table management
- ✅ Type checking and inference
- ✅ LLVM IR generation
- ✅ Support for both file input and STDIN input

---

## Project Structure

```
rcompiler2025/
├── src/                           # Source code
│   ├── Main.java                 # Entry point
│   ├── Lexer.java                # Lexical analysis (1304 lines)
│   ├── Parser.java               # Syntax analysis (1865 lines)
│   ├── Semantics.java            # Semantic analysis (2352 lines)
│   └── IR.java                   # Code generation (2174 lines)
├── builtin/                      # Built-in runtime support
│   ├── builtin.c                 # C runtime functions
│   ├── builtin.ll                # LLVM IR for builtins
│   └── builtin.o                 # Compiled object file
├── Makefile                      # Build configuration
├── run_tests.sh                  # Test runner script
└── README.md                     # This file
```

---

## Architecture

### Compilation Pipeline

```
Source Code (Rust)
        ↓
    [Lexer]          → Tokenization
        ↓
   Token Stream
        ↓
    [Parser]         → AST Construction
        ↓
    Abstract Syntax Tree (AST)
        ↓
    [Semantics]      → Type Checking, Scope Analysis
        ↓
    Annotated AST
        ↓
      [IR]           → LLVM IR Generation
        ↓
    LLVM IR (.ll)
        ↓
    [clang]          → Machine Code Generation
        ↓
    Executable
```

### Module Overview

#### **Lexer** (`Lexer.java`, 1304 lines)
- Tokenizes Rust source code
- Handles:
  - Identifiers and keywords (strict & reserved)
  - Number literals (integers, floats with suffixes)
  - String and character literals
  - Operators and delimiters
  - Comments (line and block)
  - Unicode identifier support
- Maintains precise position information for error reporting

#### **Parser** (`Parser.java`, 1865 lines)
- Recursive descent parser
- Constructs Abstract Syntax Tree (AST)
- Handles:
  - Function definitions
  - Variable bindings (`let` statements)
  - Expression parsing (binary, unary, calls, indexing, etc.)
  - Type annotations
  - Control flow (if/else, while, loop, match)
  - Struct/Enum/Trait/Impl items
  - Block expressions
- Comprehensive error recovery

#### **Semantics** (`Semantics.java`, 2352 lines)
- Semantic analysis and validation
- Performs:
  - Scope tree construction
  - Symbol table management (types and values)
  - Type checking
  - Variable binding validation
  - Trait and impl analysis
  - Break/continue context validation
- Maintains scope hierarchy for nested items

#### **IR** (`IR.java`, 2174 lines)
- Generates LLVM Intermediate Representation
- Implements IR instructions:
  - Binary operations
  - Memory operations (alloca, load, store)
  - Control flow (br, ret, phi)
  - Function calls
  - Comparisons (icmp)
  - GEP (getelementptr)
- Produces standard LLVM IR that can be processed by `clang`

---

## Building & Running

### Prerequisites

- **Java**: JDK 8 or higher
- **Make**: For build automation
- **LLVM**: `clang` (for final code generation, optional for testing)

### Build

```bash
# Compile the compiler
make build

# Or manually:
mkdir -p out
find src -name "*.java" -print0 | xargs -0 javac -d out
```

### Run

```bash
# Method 1: From STDIN (recommended for testing)
make run

# Method 2: From file
java -cp out rcompiler2025.src.Main input.rx

# Method 3: Manually
java -cp out rcompiler2025.src.Main < input.rx
```

### Clean

```bash
make clean  # Remove compiled classes
```

---

## Usage

### Basic Example

Create a file `hello.rx`:
```rust
fn main() {
    let x: i32 = 5;
    let y: i32 = 10;
}
```

Compile it:
```bash
java -cp out rcompiler2025.src.Main hello.rx > hello.ll
```

Or using stdin:
```bash
cat hello.rx | make run > hello.ll
```

### Input/Output

**Input**: Rust source code (from file or STDIN)
- **Via file**: `java -cp out rcompiler2025.src.Main input.rx`
- **Via STDIN**: `cat input.rx | java -cp out rcompiler2025.src.Main`

**Output**:
- **STDOUT**: LLVM IR code (if compilation succeeds)
- **STDERR**: Error messages and diagnostics
- **Exit Code**: 
  - `0` = Compilation successful (passed semantic analysis)
  - `1` = Compilation failed (lexical/syntax/semantic error)

---

## Compilation Pipeline

### Phase 1: Lexical Analysis
- Reads source code character by character
- Produces a stream of tokens
- Detects lexical errors (invalid characters, malformed literals)

### Phase 2: Syntax Analysis  
- Consumes token stream
- Builds Abstract Syntax Tree (AST)
- Validates grammar and syntax
- Detects syntax errors (unexpected tokens, missing delimiters)

### Phase 3: Semantic Analysis
- Traverses AST
- Builds scope hierarchy
- Performs type checking
- Validates variable usage and declarations
- Detects semantic errors (undefined variables, type mismatches)

### Phase 4: Code Generation
- Traverses annotated AST
- Generates LLVM IR instructions
- Manages virtual registers and basic blocks
- Produces `.ll` file

---

## Supported Rust Features

### Type System
- Primitive types: `i32`, `u32`, `isize`, `usize`, `bool`, `char`, `str`
- Reference types: `&T`, `&mut T`
- Array types: `[T; N]`
- Unit type: `()`
- User-defined types: `struct`, `enum`
- Trait types

### Expressions
- **Literals**: integers, floats, strings, characters, booleans
- **Binary operations**: `+`, `-`, `*`, `/`, `%`, `&&`, `||`, `==`, `!=`, `<`, `>`, etc.
- **Unary operations**: `-x`, `!x`, `&x`, `*x`
- **Function calls**: `func()`, `obj.method()`
- **Indexing**: `arr[i]`, `str[i]`
- **Field access**: `obj.field`
- **Control flow**: `if cond { ... }`, `while cond { ... }`, `loop { ... }`
- **Blocks**: `{ stmt; expr }`

### Statements
- Variable binding: `let x: Type = init;`
- Expression statements
- Item declarations (nested functions, structs, etc.)

### Items
- Function definitions: `fn name(params) -> Type { body }`
- Struct definitions: `struct Name { fields }`
- Enum definitions: `enum Name { variants }`
- Const items: `const NAME: Type = value;`
- Trait definitions: `trait Name { methods }`
- Impl blocks: `impl Trait for Type { ... }`

---

## Testing

### Run Tests

```bash
# Automated test suite
./run_tests.sh

# Results are saved in result/ directory
cat result/total_result.txt
```

---

## Error Handling

The compiler provides detailed error messages with precise location information:

```
Error: Syntax analysis failed
Location: line 5, column 12
```

Errors from all phases (lexical, syntax, semantic) are reported to STDERR and cause the compiler to exit with code 1.

---



## Performance Notes

- Compilation is single-pass with semantic analysis
- Memory usage is proportional to program size and scope depth
- Suitable for educational purposes and moderate-size Rust programs

---



## References

- [Rust Language Reference](https://doc.rust-lang.org/reference/)
- [LLVM Language Reference](https://llvm.org/docs/LangRef/)
- [Compiler Design (Dragon Book)](https://en.wikipedia.org/wiki/Compilers:_Principles,_Techniques,_and_Tools)

---

## Authors

- **Author**: Granthy
- **Course**: ACM Compiler Project (Autumn 2025)
- **University**: Shanghai Jiao Tong University

