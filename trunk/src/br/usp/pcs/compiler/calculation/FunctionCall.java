package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class FunctionCall implements Expression {
	
	private final Function function;
	private final Variable[] argVars;
	private Calculation[] argValues;
	private int argument = 0;
	
	public FunctionCall(Function function) {
		this.function = function;
		this.argVars = function.getArguments();
		this.argValues = new Calculation[argVars.length];
	}
	
	public void processArgument(Expression e) {
		if (isComplete())
			throw new CompilationException("too many arguments for function");
		
		if (!ExpressionUtils.isCompatibleArgument(argVars[argument].getType(), e.getType()))
			throw new CompilationException("argument type is not compatible");
		
		argValues[argument] = e;
		argument++;
	}
	
	public boolean isComplete() {
		return argument == argVars.length;
	}
	
	public void evaluate(CompilationUnit cu) {
		for (int i = 0; i < argVars.length; i++) {
			argValues[i].evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.STORE, argVars[i].getAddress()));
		}
		cu.cb.addInstruction(new Instruction(Opcode.CALL, function.getAddress()));
	}
	
	public Type getType() {
		return function.getReturnType();
	}

}
