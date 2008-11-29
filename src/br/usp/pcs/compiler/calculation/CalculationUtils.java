package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.memory.CodeBuffer;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class CalculationUtils {
	
	static class Constant implements Calculation {
		private final int value;
		
		public Constant(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public void evaluate(CompilationUnit cu) {
			if (Instruction.fits12Bits(value)) {
				cu.cb.addInstruction(new Instruction(Opcode.LOAD_VALUE, value));
			} else if (Instruction.fits16Bits(value)) {
				String c = cu.vm.getConstant(value);
				cu.cb.addInstruction(new Instruction(Opcode.LOAD, c));
			} else {
				throw new IllegalArgumentException("too big constant: " + value);
			}
		}
	}
	
	// evaluate the address of a label
	static class MemoryAddress implements Calculation {
		private final String address;
		public MemoryAddress(String address) {
			this.address = address;
		}
		public String getAddress() {
			return address;
		}
		public void evaluate(CompilationUnit cu) {
			String pointer = cu.mm.allocPointer("p", address);
			cu.cb.addInstruction(new Instruction(Opcode.LOAD, pointer));
		}
	}
	
	// evaluate the memory contents under a label
	private static class MemoryReference implements Calculation {
		private final String address;
		public MemoryReference(String address) {
			this.address = address;
		}
		public String getAddress() {
			return address;
		}
		public void evaluate(CompilationUnit cu) {
			cu.cb.addInstruction(new Instruction(Opcode.LOAD, address));
		}
	}
	
	private abstract static class BinaryOperation implements Calculation {
		protected Calculation op1;
		protected Calculation op2;
		
		public BinaryOperation(Calculation op1, Calculation op2) {
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public void evaluate(CompilationUnit cu) {
			op2.evaluate(cu);
			String tmp = cu.vm.takeTemporary();
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			op1.evaluate(cu);
			doOperation(cu.cb, tmp);
			cu.vm.returnTemporary(tmp);
		}
		
		protected abstract void doOperation(CodeBuffer cb, String other); 
	}
	
	private abstract static class BinaryOperationWithConstant implements Calculation {
		protected int constant;
		protected Calculation op;
		
		public BinaryOperationWithConstant(int constant, Calculation op) {
			this.constant = constant;
			this.op = op;
		}
		
		public int getConstant() {
			return constant;
		}
		
		public void setConstant(int constant) {
			this.constant = constant;
		}
		
		public void evaluate(CompilationUnit cu) {
			op.evaluate(cu);
			String tmp = cu.vm.takeTemporary();
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			constant(constant).evaluate(cu);
			doOperation(cu.cb, tmp);
			cu.vm.returnTemporary(tmp);
		}
		
		protected abstract void doOperation(CodeBuffer cb, String other); 
	}
	
	private static class AddOperation extends BinaryOperation {
		public AddOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.ADD, other));
		}
	}
	
	private static class AddOperationC extends BinaryOperationWithConstant {
		public AddOperationC(int constant, Calculation op) {
			super(constant, op);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.ADD, other));
		}
	}
	
	private static class SubtractOperation extends BinaryOperation {
		public SubtractOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.SUBTRACT, other));
		}
	}
	
	private static class SubtractOperationC extends AddOperationC {
		public SubtractOperationC(int constant, Calculation op) {
			super(constant, op);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.SUBTRACT, other));
		}
	}
	
	private static class MultiplyOperation extends BinaryOperation {
		public MultiplyOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.MULTIPLY, other));
		}
	}
	
	private static class MultiplyOperationC extends BinaryOperationWithConstant {
		public MultiplyOperationC(int constant, Calculation op) {
			super(constant, op);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.MULTIPLY, other));
		}
	}
	
	private static class DivideOperation extends BinaryOperation {
		public DivideOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		protected void doOperation(CodeBuffer cb, String other) {
			cb.addInstruction(new Instruction(Opcode.DIVIDE, other));
		}
	}

	public static Calculation add(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant(const1.getValue() + const2.getValue());
		} else if (isConstant(c1)) {
			int constant = ((Constant) c1).getValue();
			if (c2 instanceof AddOperationC) {
				((AddOperationC) c2).constant += constant;
				return c2;
			} else {
				return new AddOperationC(constant, c2);
			}
		} else if (isConstant(c2)) {
			int constant = ((Constant) c2).getValue();
			if (c1 instanceof AddOperationC) {
				((AddOperationC) c1).constant += constant;
				return c1;
			} else {
				return new AddOperationC(constant, c1);
			}
		} else {
			return new AddOperation(c1, c2);
		}
	}

	public static Calculation subtract(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant(const1.getValue() - const2.getValue());
		} else if (isConstant(c1)) {
			int constant = ((Constant) c1).getValue();
			if (c2 instanceof AddOperationC) {
				AddOperationC addOp = (AddOperationC) c2;
				int newConstant = constant - ((AddOperationC) c2).constant;
				if (addOp instanceof SubtractOperationC)
					return new AddOperationC(newConstant, addOp.op);
				else
					return new SubtractOperationC(newConstant, addOp.op);
			} else {
				return new SubtractOperationC(constant, c2);
			}
		} else if (isConstant(c2)) {
			int constant = ((Constant) c2).getValue();
			if (c1 instanceof AddOperationC) {
				((AddOperationC) c1).constant -= constant;
				return c1;
			} else {
				return new AddOperationC(-constant, c2);
			}
		} else {
			return new SubtractOperation(c1, c2);
		}
	}

	public static Calculation multiply(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant(const1.getValue() * const2.getValue());
		} else if (isConstant(c1)) {
			int constant = ((Constant) c1).getValue();
			if (c2 instanceof MultiplyOperationC) {
				((MultiplyOperationC) c2).constant *= constant;
				return c2;
			} else {
				return new MultiplyOperationC(constant, c2);
			}
		} else if (isConstant(c2)) {
			int constant = ((Constant) c2).getValue();
			if (c1 instanceof MultiplyOperationC) {
				((MultiplyOperationC) c1).constant *= constant;
				return c1;
			} else {
				return new MultiplyOperationC(constant, c1);
			}
		} else {
			return new MultiplyOperation(c1, c2);
		}
	}

	public static Calculation divide(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant(const1.getValue() / const2.getValue());
		} else{
			return new DivideOperation(c1, c2);
		}
	}
	
	public static class InverseOperation implements Calculation {
		private final Calculation c;
		public InverseOperation(Calculation c) {
			this.c = c;
		}
		public void evaluate(CompilationUnit cu) {
			c.evaluate(cu);
			String tmp = cu.vm.takeTemporary();
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			constant(0).evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.SUBTRACT, tmp));
			cu.vm.returnTemporary(tmp);
		}
	}
	
	public static class NotOperation implements Calculation {
		private final Calculation c;
		public NotOperation(Calculation c) {
			this.c = c;
		}
		public void evaluate(CompilationUnit cu) {
			c.evaluate(cu);
			String l1 = cu.mm.label("not1");
			cu.cb.addInstruction(new Instruction(Opcode.JZ, l1));
			constant(0).evaluate(cu);
			String l2 = cu.mm.label("not2");
			cu.cb.addInstruction(new Instruction(Opcode.JP, l2));
			cu.cb.setNextLabel(l1);
			constant(1).evaluate(cu);
			cu.cb.setNextLabel(l2);
		}
	}
	
	public static Calculation inverse(Calculation c) {
		if (isConstant(c)) {
			return new Constant(-getValue(c));
		}
		
		return new InverseOperation(c);
	}
	
	public static Calculation not(Calculation c) {
		if (isConstant(c)) {
			return new Constant((getValue(c) != 0) ? 1 : 0);
		}
		
		return new NotOperation(c);
	}

	public static Calculation memoryAddress(String address) {
		return new MemoryAddress(address);
	}

	public static Calculation memoryReference(String address) {
		return new MemoryReference(address);
	}

	public static Calculation constant(int value) {
		return new Constant(value);
	}

	public static boolean isConstant(Calculation c) {
		return c instanceof Constant;
	}

	public static int getValue(Calculation c) {
		if (!isConstant(c)) throw new IllegalStateException("not a constant");
		return ((Constant) c).getValue();
	}
	
	private static class LessThanOperation implements Calculation {
		protected Calculation op1;
		protected Calculation op2;
		
		public LessThanOperation(Calculation op1, Calculation op2) {
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public void evaluate(CompilationUnit cu) {
			op2.evaluate(cu);
			String tmp = cu.vm.takeTemporary();
			cu.cb.addInstruction(new Instruction(Opcode.STORE, tmp));
			op1.evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.SUBTRACT, tmp));
			cu.vm.returnTemporary(tmp);
			String label = cu.mm.label("lt");
			cu.cb.addInstruction(new Instruction(Opcode.JN, label));
			constant(0).evaluate(cu);
			cu.cb.setNextLabel(label);
		}
	}
	
	public static Calculation lessThan(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant((const1.getValue() < const2.getValue()) ? 1 : 0);
		} else {
			return new LessThanOperation(c1, c2);
		}
	}
	
	private static class LogicalAndOperation implements Calculation {
		protected Calculation op1;
		protected Calculation op2;
		
		public LogicalAndOperation(Calculation op1, Calculation op2) {
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public void evaluate(CompilationUnit cu) {
			op1.evaluate(cu);
			String label = cu.mm.label("and");
			cu.cb.addInstruction(new Instruction(Opcode.JZ, label));
			op2.evaluate(cu);
			cu.cb.setNextLabel(label);
		}
	}
	
	public static Calculation logicalAnd(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant((const1.getValue() != 0 && const2.getValue() != 0) ? 1 : 0);
		} else {
			return new LogicalAndOperation(c1, c2);
		}
	}
	
	private static class LogicalOrOperation implements Calculation {
		protected Calculation op1;
		protected Calculation op2;
		
		public LogicalOrOperation(Calculation op1, Calculation op2) {
			this.op1 = op1;
			this.op2 = op2;
		}
		
		public void evaluate(CompilationUnit cu) {
			op1.evaluate(cu);
			String l1 = cu.mm.label("or1");
			cu.cb.addInstruction(new Instruction(Opcode.JZ, l1));
			String l2 = cu.mm.label("or2");
			cu.cb.addInstruction(new Instruction(Opcode.JP, l2));
			cu.cb.setNextLabel(l1);
			op2.evaluate(cu);
			cu.cb.setNextLabel(l2);
		}
	}
	
	public static Calculation logicalOr(Calculation c1, Calculation c2) {
		if (isConstant(c1) && isConstant(c2)) {
			Constant const1 = (Constant) c1;
			Constant const2 = (Constant) c2;
			return new Constant((const1.getValue() != 0 || const2.getValue() != 0) ? 1 : 0);
		} else {
			return new LogicalOrOperation(c1, c2);
		}
	}

}
