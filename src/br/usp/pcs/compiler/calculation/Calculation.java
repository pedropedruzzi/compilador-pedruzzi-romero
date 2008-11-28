package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.memory.CodeBuffer;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.InternalVariableManager;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public abstract class Calculation {
	
	public abstract void compute(CodeBuffer cb, InternalVariableManager vm);
	
	public boolean isConstant() {
		return this instanceof Constant;
	}
	
	static class Constant extends Calculation {
		private final int value;
		
		public Constant(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public void compute(CodeBuffer cb, InternalVariableManager vm) {
			if (value >> 12 == 0) {
				cb.addInstruction(new Instruction(Opcode.LOAD_VALUE, value));
			} else if (value >> 16 == 0) {
				String c = vm.getConstant(value);
				cb.addInstruction(new Instruction(Opcode.LOAD, c));
			} else {
				throw new IllegalArgumentException("too big constant: " + value);
			}
		}
	}
	
	static class MemoryReference extends Calculation {
		private final String address;
		
		public MemoryReference(String address) {
			this.address = address;
		}
		
		public void compute(CodeBuffer cb, InternalVariableManager vm) {
			// dá pra resolver isso... se o cara quiser realmente computar
			// cria-se aqui o ponteiro: mm.allocPointer(null, address)
			throw new UnsupportedOperationException("holy shit!");
		}
	}
	
	static class AddressableMemoryReference extends MemoryReference {
		private String pointerAddress;
		
		public AddressableMemoryReference(String address, String pointerAddress) {
			super(address);
			this.pointerAddress = pointerAddress;
		}
		
		public void compute(CodeBuffer cb, InternalVariableManager vm) {
			cb.addInstruction(new Instruction(Opcode.LOAD, pointerAddress));
		}
	}
	
	abstract static class BinaryOperation extends Calculation {
		protected Calculation op1;
		protected Calculation op2;
		
		public BinaryOperation(Calculation op1, Calculation op2) {
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public void compute(CodeBuffer cb, InternalVariableManager vm) {
			op1.compute(cb, vm);
			String tmp = vm.takeTemporary();
			cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			op2.compute(cb, vm);
			doOperation(cb, tmp);
			vm.returnTemporary(tmp);
		}
		
		protected abstract void doOperation(CodeBuffer cb, String other); 
	}
	
	static class AddOperation extends BinaryOperation {
		public AddOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.ADD, other));
		}
	}
	
	static class SubtractOperation extends BinaryOperation {
		public SubtractOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.SUBTRACT, other));
		}
	}
	
	static class MultiplyOperation extends BinaryOperation {
		public MultiplyOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.MULTIPLY, other));
		}
	}
	
	static class DivideOperation extends BinaryOperation {
		public DivideOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.DIVIDE, other));
		}
	}

	public static Calculation add(Calculation c1, Calculation c2) {
		if (c1.isConstant() && c2.isConstant()) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant(const1.getValue() + const2.getValue());
		} else if (c1.isConstant()) {
			Constant const1 = (Constant) c1;
			if (c2 instanceof AddOperation) {
				AddOperation add = (AddOperation) c2;
				if (add.op1.isConstant()) {
					Constant const2 = (Constant) add.op1;
					add.op1 = new Constant(const1.getValue() + const2.getValue());
					return c2;
				} else if (add.op2.isConstant()) {
					Constant const2 = (Constant) add.op2;
					add.op2 = new Constant(const1.getValue() + const2.getValue());
					return c2;
				} 
			}
		} else if (c2.isConstant()) {
			Constant const1 = (Constant) c2;
			if (c1 instanceof AddOperation) {
				AddOperation add = (AddOperation) c1;
				if (add.op1.isConstant()) {
					Constant const2 = (Constant) add.op1;
					add.op1 = new Constant(const1.getValue() + const2.getValue());
					return c1;
				} else if (add.op2.isConstant()) {
					Constant const2 = (Constant) add.op2;
					add.op2 = new Constant(const1.getValue() + const2.getValue());
					return c1;
				} 
			}
		}
		
		return new AddOperation(c1, c2);
	}

}
