package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;

public class TypedCalculation implements Expression {

	private final Type type;
	private final Calculation c;

	public TypedCalculation(Calculation c, Type type) {
		this.c = c;
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void evaluate(CompilationUnit cu) {
		c.evaluate(cu);
	}
	
	public void branchIfZero(CompilationUnit cu, String label) {
		c.branchIfZero(cu, label);
	}

}
