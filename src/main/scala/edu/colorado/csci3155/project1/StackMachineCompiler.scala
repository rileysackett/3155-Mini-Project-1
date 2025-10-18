package edu.colorado.csci3155.project1

object StackMachineCompiler {



    /* Function compileToStackMachineCode
        Given expression e as input, return a corresponding list of stack machine instructions.
        The type of stackmachine instructions are in the file StackMachineEmulator.scala in this same directory
        The type of Expr is in the file Expr.scala in this directory.

        TODO: Implement this function.
     */
    def compileToStackMachineCode(e: Expr): List[StackMachineInstruction] = {
        e match
            case Id(id) => List(ILoad(id))
            case Const(d) => List(IPush(d))
            case Add(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IPlus)
            case Sub(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(ISub)
            case Mul(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IMul)
            case Div(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IDiv)
            case Geq(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IGeq)
            case Gt(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IGt)
            case Eq(l, r) => compileToStackMachineCode(l) ++ compileToStackMachineCode(r) ++ List(IEq)
            case Not(x) => compileToStackMachineCode(x) ++ List(INot)
            case Let(id, e1, e2) => 
                val v1 = compileToStackMachineCode(e1)
                val v2 = compileToStackMachineCode(e2)
                v1 ++ List(IStore(id)) ++ v2 ++ List(IPop)
            case _ => throw new Exception("Not implemented yet")
    }

}
