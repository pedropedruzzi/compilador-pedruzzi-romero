package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.calculation.Calculation.Constant;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.IntType;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Record;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CodeBuffer;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.InternalVariableManager;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class Expression {
	
	/**
	 * CARALHO.. não faço a menor idéia de como implementar isso.
	 * 
	 * Separei a árvore de evaluation (Calculation) das coisas de verificação de tipo.
	 * Fez sentido.. Mas agora ta tudo confuso! Não sei como montar a árvore e ir checando os tipos.
	 */
	
	private Calculation calculation;
	private Type type;
	
	public void add(Expression e) {
		if (!isCompatible(type, e.getType())) throw new IllegalArgumentException("incompatible types");
		calculation = Calculation.add(calculation, e.calculation);
	}
	
	public void add(int i) {
		if (!(type instanceof IntType)) throw new IllegalArgumentException("incompatible types");
		if (isConstant()) {
			calculation = new Calculation.Constant(getValue() + i);
		} else {
			calculation = new Calculation.AddOperation(calculation, new Calculation.Constant(i));
		}
	}
	
	public void subtract(Expression e) {
		if (!isCompatible(type, e.getType())) throw new IllegalArgumentException("incompatible types");
		if (this.isConstant() && e.isConstant()) {
			calculation = new Calculation.Constant(this.getValue() - e.getValue());
		} else {
			calculation = new Calculation.SubtractOperation(this.calculation, e.calculation);
		}
	}
	
	public void accessArrayElement(Expression e) {
		if (!(type instanceof Array)) throw new IllegalStateException("not an array");
		if (!(e.getType() instanceof IntType)) throw new IllegalArgumentException("array index must be and integer");
		type = ((Array) type).getInnerType();
		add(type.sizeOf());
	}
	
	public void accessRecordMember(String id) {
		if (!(type instanceof Record)) throw new IllegalStateException("not a record");
		Record r = (Record) type;
		if (!r.containsField(id)) throw new IllegalStateException("field does not exist");
		
		type = r.getField(id).getType();
		add(r.getOffset(id));
	}

	public boolean isConstant() {
		return calculation == null || calculation instanceof Constant;
	}

	public boolean isConstantCompileTimeOnly() {
		return isConstant();
	}

	public int getValue() {
		if (!isConstant()) throw new IllegalStateException("not a constant");
		int val;
		if (calculation == null) val = 0;
		else val = ((Constant) calculation).getValue();
		return val;
	}
	
	public Type getType() {
		return type;
	}
	
	public void evaluate(CodeBuffer cb, InternalVariableManager vm) {
		if (calculation == null)
			cb.addInstruction(new Instruction(Opcode.HALT, 0)); // TODO: tirar esta porra!
		else
			calculation.compute(cb, vm);
	}

	public static boolean isCompatible(Type t1, Type t2) {
		return t1 == t2 && t1 instanceof PrimitiveType;
	}

}
