package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.calculation.CalculationUtils.Constant;
import br.usp.pcs.compiler.calculation.CalculationUtils.MemoryAddress;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.SizedArray;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;

public class ExpressionUtils {
	
	public static Expression add(Expression e1, Expression e2) {
		if (!isCompatibleMathOperation(e1.getType(), e2.getType()))
			throw new CompilationException("incompatible types\nt1 = " + e1.getType().toString() + "\nt2 = " + e2.getType().toString());
		return wrap(CalculationUtils.add(e1, e2), e1.getType());
	}
	
	private static Expression wrap(Calculation c, Type type) {
		if (CalculationUtils.isConstant(c))
			return integerConstant(CalculationUtils.getValue(c));
		else
			return new TypedCalculation(c, type);
	}

	public static Expression subtract(Expression e1, Expression e2) {
		if (!isCompatibleMathOperation(e1.getType(), e2.getType()))
			throw new CompilationException("incompatible types");
		return new TypedCalculation(CalculationUtils.subtract(e1, e2), e1.getType());
	}

	public static Expression multiply(Expression e1, Expression e2) {
		if (!isCompatibleMathOperation(e1.getType(), e2.getType()))
			throw new CompilationException("incompatible types");
		return wrap(CalculationUtils.multiply(e1, e2), e1.getType());
	}

	public static boolean isCompatible(Type t1, Type t2) {
		return t1 == t2 && t1 instanceof PrimitiveType;
	}

	public static boolean isCompatibleArgument(Type arg, Type val) {
		if (arg == val) return true;
		if (arg instanceof Array && val instanceof Array) {
			Array a1 = (Array) arg;
			Array a2 = (Array) val;
			return equalType(a1.getInnerType(), a2.getInnerType());
		} else return false;
	}

	private static boolean equalType(Type t1, Type t2) {
		if (t1 == t2) return true;
		else if (t1 instanceof SizedArray && t2 instanceof SizedArray) {
			SizedArray a1 = (SizedArray) t1;
			SizedArray a2 = (SizedArray) t2;
			return a1.getLength() == a2.getLength() && equalType(a1.getInnerType(), a2.getInnerType());
		} else return false;
	}

	public static boolean isCompatibleMathOperation(Type t1, Type t2) {
		return isCompatibleAssignment(t1, t2);
	}

	public static boolean isCompatibleAssignment(Type t1, Type t2) {
		return t1 == t2 && t1 instanceof PrimitiveType;
	}

	public static boolean isCompatibleInitializer(Type t1, Type t2) {
		if (t1 == PrimitiveType.intTypeInstance()) {
			return t2 == PrimitiveType.intTypeInstance();
		} else if (t1 instanceof Array) {
			return (t2 instanceof Array) && 
				((Array) t1).getInnerType() == PrimitiveType.charTypeInstance() && 
				((Array) t2).getInnerType() == PrimitiveType.charTypeInstance();
		} else return false;
	}
	
	public static class IntegerConstant extends Constant implements Expression {
		public IntegerConstant(int value) {
			super(value);
		}
		public Type getType() {
			return PrimitiveType.intTypeInstance();
		}
	}
	
	public static class StringConstant extends MemoryAddress implements Expression {
		private final Type type = new Array(PrimitiveType.charTypeInstance());
		public StringConstant(String address) {
			super(address);
		}
		public Type getType() {
			return type;
		}
	}

	public static Expression integerConstant(int value) {
		return new IntegerConstant(value);
	}

	public static Expression stringConstant(String address) {
		return new StringConstant(address);
	}
	
	public static Expression inverse(Expression e) {
		if (e.getType() != PrimitiveType.intTypeInstance())
			throw new CompilationException("inverse operator only applicable to integers");
		return wrap(CalculationUtils.inverse(e), e.getType());
	}
	
	public static Expression not(Expression e) {
		if (e.getType() != PrimitiveType.intTypeInstance())
			throw new CompilationException("logical not operator only applicable to integers");
		return wrap(CalculationUtils.not(e), e.getType());
	}

	private static class VoidExpression implements Expression {
		public Type getType() {
			return null;
		}
		public void evaluate(CompilationUnit cu) {
			throw new CompilationException("can't evaluate a void expression");
		}
	}
	private static final Expression VOID = new VoidExpression();
	public static Expression voidExpression() {
		return VOID;
	}

	public static Expression memoryReference(String address, Type type) {
		return wrap(CalculationUtils.memoryReference(address), type);
	}

}
