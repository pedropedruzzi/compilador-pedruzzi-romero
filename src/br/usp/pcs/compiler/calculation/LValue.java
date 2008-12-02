package br.usp.pcs.compiler.calculation;

import java.util.HashSet;
import java.util.Set;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.entity.Variable;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.IntType;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Record;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

/**
 * A lvalue is a pointer and always evaluates to an address, because it may be
 * on the left side of an assignment.
 * 
 * Esta porra tem dois usos:
 * 1. aparecer do lado direito da expressao. Eu quero saber o valor! Se ainda não chegou no primitivo pode ser o valor do ponteiro!
 * 2. do lado esquerdo da expressao. Neste caso eu vou querer atribuir coisas na variavel.
 * 
 * O certo é o método evaluate retornar o valor pro caso 1. E inventar outro método aki que processa a atribuição.
 */
public class LValue extends BasicCalculation implements Expression {

	private final short LOAD = (short) 0x8000;
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
			throw new CompilationException(type.toString() + "is not an array type");
		if (!(e.getType() instanceof IntType))
			throw new CompilationException("array index must be an integer " + e.getType());
		
		type = ((Array) type).getInnerType();
		
		if (CalculationUtils.isConstant(e)) offset += CalculationUtils.getValue(e) * type.sizeOf();
		else products.add(CalculationUtils.multiply(CalculationUtils.constant(type.sizeOf()), e));
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
		return type;
	}
	
	public boolean isPrimitiveVariable() {
		return var.getType() instanceof PrimitiveType;
	}
	
	/**
	 * variavel primitiva: lê o valor da variável
	 * variavel não-primitiva
	 *     lvalue primitiva: avalia o valor
	 *     lvalue não-primitiva: avalia o endereço
	 */
	public void evaluate(CompilationUnit cu) {
		if (isPrimitiveVariable()) {
			cu.cb.addInstruction(new Instruction(Opcode.LOAD, var.getAddress()));
		} else if (type instanceof PrimitiveType) {
			CalculationUtils.constant(LOAD).evaluate(cu);
			String loadI = cu.mm.label("load");
			cu.cb.addInstruction(new Instruction(Opcode.STORE, loadI));
			evaluateAddress(cu);
			cu.cb.addInstruction(new Instruction(Opcode.ADD, loadI));
			cu.cb.addInstruction(new Instruction(loadI, Opcode.CONSTANT, 0));
		} else {
			evaluateAddress(cu);
		}
	}
	
	public void evaluateAddress(CompilationUnit cu) {
		if (offset != 0) {
			CalculationUtils.constant(offset).evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.ADD, var.getAddress()));
		} else {
			cu.cb.addInstruction(new Instruction(Opcode.LOAD, var.getAddress()));
		}
		String tmp = cu.vm.takeTemporary();
		for (Calculation c : products) {
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			c.evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.ADD, tmp));
		}
		cu.vm.returnTemporary(tmp);
	}

}
