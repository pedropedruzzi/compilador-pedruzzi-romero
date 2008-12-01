package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.memory.CompilationUnit;

public interface Calculation {
	
	public void evaluate(CompilationUnit cu);
	public void branchIfZero(CompilationUnit cu, String label);
	
}
