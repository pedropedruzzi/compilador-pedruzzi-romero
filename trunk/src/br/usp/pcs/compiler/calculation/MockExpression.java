package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class MockExpression implements Expression {

	public Type getType() {
		return PrimitiveType.intTypeInstance();
	}

	public void evaluate(CompilationUnit cu) {
		cu.cb.addInstruction(new Instruction(Opcode.OS, 666));
	}

}
