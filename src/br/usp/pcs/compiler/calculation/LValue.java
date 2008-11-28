package br.usp.pcs.compiler.calculation;

import java.util.HashSet;
import java.util.Set;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.entity.Variable;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.IntType;
import br.usp.pcs.compiler.entity.type.Pointer;
import br.usp.pcs.compiler.entity.type.Record;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class LValue implements Expression {
	
	private final Variable var;
	private Type type;
	private int offset;
	private Set<Calculation> products = new HashSet<Calculation>();
	
	public LValue(Variable var) {
		this.var = var;
		this.type = var.getType();
	}
	
	public void accessArrayElement(Expression e) {
		if (!(type instanceof Array))
			throw new CompilationException("not an array");
		if (!(e.getType() instanceof IntType))
			throw new CompilationException("array index must be an integer");
		type = ((Array) type).getInnerType();
		products.add(CalculationUtils.multiply(CalculationUtils.constant(type.sizeOf()), e));
	}

	public void accessRecordMember(String id) {
		if (!(type instanceof Record))
			throw new CompilationException("not a record");
		Record r = (Record) type;
		if (!r.containsField(id))
			throw new CompilationException("field does not exist");
		
		type = r.getField(id).getType();
		offset += r.getOffset(id);
	}
	
	public Type getType() {
		return new Pointer(type);
	}
	
	public Type getInnerType() {
		return type;
	}
	
	public void evaluate(CompilationUnit cu) {
		CalculationUtils.constant(offset).evaluate(cu);
		cu.cb.addInstruction(new Instruction(Opcode.ADD, var.getAddress()));
		String tmp = cu.vm.takeTemporary();
		for (Calculation c : products) {
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			c.evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.ADD, tmp));
		}
		cu.vm.returnTemporary(tmp);
	}

}
