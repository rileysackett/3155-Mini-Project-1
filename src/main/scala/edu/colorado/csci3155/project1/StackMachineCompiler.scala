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
                val init = compileToStackMachineCode(e1)
                val body = compileToStackMachineCode(e2)
                init ++ List(IStore(id)) ++ body ++ List(IPop)
            case IfThenElse(cond, tExpr, elseExpr) =>
                val condCode = compileToStackMachineCode(cond)
                val thenCode = compileToStackMachineCode(tExpr)
                val elseCode = compileToStackMachineCode(elseExpr)
                condCode ++ List(ICondSkip(thenCode.length + 1)) ++ thenCode ++ List(ISkip(elseCode.length)) ++ elseCode
            case And(l, r) =>
                val lc = compileToStackMachineCode(l)
                val rc = compileToStackMachineCode(r)
                lc ++ List(ICondSkip(rc.length + 1)) ++ rc ++ List(ISkip(1)) ++ List(IPushBool(false))
            case Or(l, r) =>
                val lc2 = compileToStackMachineCode(l)
                val rc2 = compileToStackMachineCode(r)
                val shortCircuit = List(IPushBool(true), ISkip(rc2.length))
                lc2 ++ List(ICondSkip(shortCircuit.length)) ++ shortCircuit ++ rc2
            case null => throw new Exception("Null expression")
    }

}
