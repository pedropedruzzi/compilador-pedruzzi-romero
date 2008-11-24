package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.symbol.type.PrimitiveType;
import br.usp.pcs.compiler.symbol.type.Type;

public abstract class Calculation {

	public static Calculation wrap(int value) {
		return new IntValueConstant(value);
	}
	
	public static Calculation memoryReference(int address, Type type) {
		return new MemoryReference(address, type);
	}

	public static Calculation sum(Object o1, Object o2) {
		return new SumOperation(wrapIfNeeds(o1), wrapIfNeeds(o2));
	}

	public static Calculation multiply(Object o1, Object o2) {
		return new MultiplyOperation(wrapIfNeeds(o1), wrapIfNeeds(o2));
	}

	private static Calculation wrapIfNeeds(Object o) {
		if (o instanceof Integer) return wrap((Integer) o);
		else if (o instanceof Calculation) return (Calculation) o;
		else throw new IllegalArgumentException("can't deal with this type: " + o.getClass().getName());
	}

	public abstract boolean isConstant();

	public abstract int getValue();
	
	public abstract Type getType();
	
	private static class IntValueConstant extends Calculation {
		private final int value;

		public IntValueConstant(int value) {
			this.value = value;
		}

		public Type getType() {
			return PrimitiveType.intTypeInstance();
		}

		public int getValue() {
			return value;
		}

		public boolean isConstant() {
			return true;
		}
	}
	
	private static class MemoryReference extends Calculation {
		private final int address;
		private final Type type;

		public MemoryReference(int address, Type type) {
			this.address = address;
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public int getValue() {
			return address;
		}

		public boolean isConstant() {
			return true;
		}
	}
	
	private abstract static class BinaryOperation extends Calculation {
		protected final Type type;
		protected final Calculation op1;
		protected final Calculation op2;

		public BinaryOperation(Calculation op1, Calculation op2) {
			type = op1.getType();
			if (type != op2.getType() || type != PrimitiveType.intTypeInstance())
				throw new UnsupportedOperationException("can only take integer operands");
			this.op1 = op1;
			this.op2 = op2;
		}

		public Type getType() {
			return type;
		}

		public boolean isConstant() {
			return op1.isConstant() && op2.isConstant();
		}
	}
	
	private static class SumOperation extends BinaryOperation {
		public SumOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		public int getValue() {
			return op1.getValue() + op2.getValue();
		}
	}
	
	private static class MultiplyOperation extends BinaryOperation {
		public MultiplyOperation(Calculation op1, Calculation op2) {
			super(op1, op2);
		}

		public int getValue() {
			return op1.getValue() * op2.getValue();
		}
	}

}
