/**
 * 
 */
package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

abstract class BasicCalculation implements Calculation {
	public void branchIfZero(CompilationUnit cu, String label) {
		evaluate(cu);
		cu.cb.addInstruction(new Instruction(Opcode.JZ, label));
	}
}