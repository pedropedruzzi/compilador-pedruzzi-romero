package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class Dereference extends BasicCalculation implements Expression {

	private final short LOAD = (short) 0x8000;
	private final LValue lvalue;

	public Dereference(LValue lvalue) {
		this.lvalue = lvalue;
	}
	
	public Type getType() {
		return lvalue.getInnerType();
	}

	public void evaluate(CompilationUnit cu) {
		CalculationUtils.constant(LOAD).evaluate(cu);
		String loadI = cu.mm.label("load");
		cu.cb.addInstruction(new Instruction(Opcode.STORE, loadI));
		lvalue.evaluate(cu);
		cu.cb.addInstruction(new Instruction(Opcode.ADD, loadI));
		cu.cb.addInstruction(new Instruction(loadI, Opcode.CONSTANT, 0));
	}

}
