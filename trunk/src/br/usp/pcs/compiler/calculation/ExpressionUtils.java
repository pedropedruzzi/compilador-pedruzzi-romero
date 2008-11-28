package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.SizedArray;
import br.usp.pcs.compiler.entity.type.Type;

public class ExpressionUtils {
	
	/**
	 * CARALHO.. não faço a menor idéia de como implementar isso.
	 * 
	 * Separei a árvore de evaluation (CalculationUtils) das coisas de verificação de tipo.
	 * Fez sentido.. Mas agora ta tudo confuso! Não sei como montar a árvore e ir checando os tipos.
	 */
	
	public Expression add(Expression e1, Expression e2) {
		if (!isCompatible(e1.getType(), e2.getType())) throw new IllegalArgumentException("incompatible types");
		return new TypedCalculation(CalculationUtils.add(e1, e2), e1.getType());
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

	public static Expression integerConstant(int value) {
		return new TypedCalculation(CalculationUtils.constant(value), PrimitiveType.intTypeInstance());
	}

	public static Expression stringConstant(String address) {
		return new TypedCalculation(CalculationUtils.memoryAddress(address), new Array(PrimitiveType.charTypeInstance()));
	}

}
