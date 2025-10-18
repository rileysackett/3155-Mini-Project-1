This repository is a small Scala (sbt) project for a course assignment implementing a stack-machine compiler
and emulator for a simple expression language. The guidance below helps an AI coding agent be productive
working on the codebase.

Repository layout and big picture
- Top-level: `build.sbt` defines Scala 3.3.6, dependencies (Scalatest) and a helper `checkAndZipSubmission` task.
- Sources: `src/main/scala/edu/colorado/csci3155/project1/` contains the compiler and emulator:
  - `Expr.scala` — AST for expressions (Const, Id, arithmetic, boolean ops, IfThenElse, Let).
  - `StackMachineCompiler.scala` — should compile `Expr` to `List[StackMachineInstruction]` (currently TODO).
  - `StackMachineEmulator.scala` — defines `StackMachineInstruction` ADT and an emulator (partially TODO).
  - `Value.scala` — runtime values (`Num`, `Bool`, `Error`).
  - `Main.scala` — small runner demonstrating compile+emulate.

Design notes an agent should know
- The compiler emits a linear list of stack-machine instructions (see tests for exact expected instruction sequences).
- The emulator executes instructions and returns a tuple (top Value, runtime environment stack). Environment is a list of (String, Value) pairs used by `IStore`/`ILoad` and `IPop` for let-binding scoping.
- Conditional and short-circuit boolean logic is implemented via two instructions: `ICondSkip(n)` and `ISkip(n)` — tests show exact offsets expected. Use tests in `src/test/scala/...` as the authoritative spec for instruction encoding.

Critical files and examples (use these when implementing/validating changes)
- Compiler expected outputs (examples): see `src/test/scala/edu/colorado/csci3155/project1/CompilerTest.scala` for many canonical instruction lists. Copy expected lists as unit-test-driven specs when changing behavior.
- Emulator behavior and operand/env conventions: see `src/test/scala/edu/colorado/csci3155/project1/StackMachineTest.scala`.
- `build.sbt` contains a custom sbt task `checkAndZipSubmission` — preserves the files to include in the zip; do not remove or rename the listed source files unless updating `build.sbt` accordingly.

Build / test / debug workflows
- To compile and run tests locally use sbt (recommended):
  - sbt compile
  - sbt test
  - To run the simple runner: sbt "runMain edu.colorado.csci3155.project1.Main"
- The project uses Scala 3; prefer IDEs (IntelliJ) configured for Scala 3 / sbt.

Project conventions and gotchas
- The test-suite is the specification. Tests assert exact List[StackMachineInstruction] values. When implementing `compileToStackMachineCode` or `emulateSingleInstruction`, match the ordering and instruction forms shown in tests (e.g., `IPush(1.0), IStore("x"), ILoad("x"), IPop`).
- Many functions are left as `???` — follow the tests for exact semantics (e.g., `IDiv` must behave like floating-point divide and throw on divide-by-zero).
- `Value` accessors throw runtime exceptions for type mismatches. Emulator and compiler should maintain correct typing of pushed values.
- The emulator's `emulateStackMachine` uses pattern matching on instructions; when adding instructions, ensure the `match` branches and skip logic for `ICondSkip`/`ISkip` are preserved.

Integration points and external deps
- Third-party: only `scalatest` and `scalactic` (test dependency declared in `build.sbt`). No network calls.

Implementation checklist for common tasks
- Implement `StackMachineInstruction` ADT in `StackMachineEmulator.scala` following tests (instructions like `IPush`, `IPushBool`, `IPlus`, `ISub`, `IMul`, `IDiv`, `IGeq`, `IGt`, `IPlus`, `IStore`, `ILoad`, `IPop`, `INot`, `IPushBool`, `ICondSkip`, `ISkip`, etc.).
- Implement `emulateSingleInstruction` to update operand and env stacks according to the instruction semantics used in tests.
- Implement `StackMachineCompiler.compileToStackMachineCode` to generate instruction lists matching tests (use recursive AST traversal and emit IPush/IPushBool, arithmetic ops, store/load/pop for Let, and skip offsets for IfThenElse and boolean short-circuiting).

How to validate changes
- Run `sbt test` — tests are the spec. If a new instruction or behavior is added, update the tests accordingly.
- Use `Main.scala` to run quick manual smoke tests: it prints compiled instructions and emulated results.

If you need clarification
- If a required behavior is not inferable from tests and sources, ask the maintainer which instruction encoding is desired. Prefer minimal changes that keep all existing tests passing.

Please review these instructions and tell me which sections you want expanded or if you'd like the agent to implement one of the TODOs (compiler or emulator) now.
