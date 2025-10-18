package edu.colorado.csci3155.project1

import scala.annotation.tailrec
import scala.math.Equiv.Double.IeeeEquiv



sealed trait StackMachineInstruction
/*-- TODO: Complete the inductive definition of the remaining 
          byte code instructions as specified 
          in the documentation --*/


case class ICondSkip(n: Int) extends StackMachineInstruction
case class ISkip(n: Int) extends StackMachineInstruction
// Instruction constructors expected by the tests
case class IPush(d: Double) extends StackMachineInstruction
case class IPushBool(b: Boolean) extends StackMachineInstruction

case object IPop extends StackMachineInstruction

case object IPlus extends StackMachineInstruction
case object ISub extends StackMachineInstruction
case object IMul extends StackMachineInstruction
case object IDiv extends StackMachineInstruction

case object IGeq extends StackMachineInstruction
case object IGt extends StackMachineInstruction
case object IEq extends StackMachineInstruction

case object INot extends StackMachineInstruction

case class IStore(name: String) extends StackMachineInstruction
case class ILoad(name: String) extends StackMachineInstruction

object StackMachineEmulator {

    /*-- An environment stack is a list of tuples containing strings and values --*/
    type EnvStack = List[(String, Value)]
    /*-- An operand stack is a list of values --*/
    type OpStack = List[Value]

    

    /* Function emulateSingleInstruction
        Given a list of values to represent a operand stack
              a list of tuples (string, value) to represent runtime stack
        and   a single instruction of type StackMachineInstruction
        Return a tuple that contains the
              modified stack that results when the instruction is executed.
              modified runtime that results when the instruction is executed.

        Make sure you handle the error cases: eg., stack size must be appropriate for the instruction
        being executed. Division by zero, log of a non negative number
        Throw an exception or assertion violation when error happens.
        TODO: Implement this function.
        
     */

    def emulateSingleInstruction(stack: OpStack, env: EnvStack, ins: StackMachineInstruction): (OpStack, EnvStack) = {
        //todo: implement this function
        //top of stack is right operand for sub/div
        //make sure to catch errors: div 0, empty stack, missing identifier for istore/load, type mismatch use getDoubleValue/getBooleanValue
    ins match
            case null => (stack, env)
            case IPush(d) => (Num(d) :: stack, env)
            case IPushBool(b) => (Bool(b) :: stack, env)
            case IPop =>
                if (stack.isEmpty) throw new Exception("Cannot pop from empty stack")
                else (stack, env.tail)
            case IPlus =>
                if (stack.length < 2) throw new Exception("Stack underflow")
                else (Num(stack.head.getDoubleValue + stack.tail.head.getDoubleValue) :: stack.drop(2), env)
            case ISub => 
                if (stack.length < 2) throw new Exception("Stack underflow")
                else (Num(stack.tail.head.getDoubleValue - stack.head.getDoubleValue) :: stack.drop(2), env)
            case IMul => 
                if (stack.length < 2) throw new Exception("Stack underflow")
                else (Num(stack.head.getDoubleValue * stack.tail.head.getDoubleValue) :: stack.drop(2), env)
            case IDiv =>
                if (stack.length < 2) throw new Exception("Stack underflow")
                else if (stack.head.getDoubleValue == 0) throw new Exception("Div by zero error")
                else (Num(stack.tail.head.getDoubleValue / stack.head.getDoubleValue) :: stack.drop(2), env)

            case IGeq => (Bool(stack.tail.head.getDoubleValue >= stack.head.getDoubleValue) :: stack.drop(2), env)
            case IGt => (Bool(stack.tail.head.getDoubleValue > stack.head.getDoubleValue) :: stack.drop(2), env)
            case IEq => (Bool(stack.tail.head.getDoubleValue == stack.head.getDoubleValue) :: stack.drop(2), env)
            case INot => (Bool(!stack.head.getBooleanValue) :: stack.tail, env)
            case IStore(id) =>
                if (stack.isEmpty) throw new Exception("Cannot pop from empty stack")
                else (stack.tail, (id, stack.head) :: env)
            case ILoad(id) =>    env.find(_._1 == id) match {  
                case Some((_, v)) => (v :: stack, env)  
                case None => throw new Exception("Unbound identifier error")}

            case _ => throw new Exception("Unknown instruction")
    }

    /* Function emulateStackMachine
       Execute the list of instructions provided as inputs using the
       emulateSingleInstruction function.
       Return the final runtimeStack and the top element of the opStack
     */
    @tailrec
    def emulateStackMachine(instructionList: List[StackMachineInstruction], 
                            opStack: OpStack=Nil, 
                            runtimeStack: EnvStack=Nil): (Value, EnvStack) =
        {
            /*-- Are we out of instructions to execute --*/
            if (instructionList.isEmpty){
                /*-- output top elt. of operand stack and the runtime stack --*/
                (opStack.head, runtimeStack)
            } else {
                /*- What is the instruction on top -*/
                val ins = instructionList.head
                ins match {
                    /*-- Conditional skip instruction --*/
                    case ICondSkip(n) => {
                        /* get the top element in operand stack */
                        val topElt = opStack.head 
                        val restOpStack = opStack.tail 
                        val b = topElt.getBooleanValue /* the top element better be a boolean */
                        if (!b) {
                            /*-- drop the next n instructions --*/
                            val restOfInstructions = instructionList.drop(n+1)
                            emulateStackMachine(restOfInstructions, restOpStack, runtimeStack)
                        } else {
                            /*-- else just drop this instruction --*/
                            emulateStackMachine(instructionList.tail, restOpStack, runtimeStack)
                        }
                    }
                    case ISkip(n) => {
                        /* -- drop this instruction and next n -- continue --*/
                        emulateStackMachine(instructionList.drop(n+1), opStack, runtimeStack)
                    }

                    case _ => {
                        /*- Otherwise, just call emulateSingleInstruction -*/
                        val (newOpStack: OpStack, newRuntime:EnvStack) = emulateSingleInstruction(opStack, runtimeStack, ins)
                        emulateStackMachine(instructionList.tail, newOpStack, newRuntime)
                    }
                }
            }
        }
}