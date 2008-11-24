package br.usp.pcs.compiler.calculation;

public abstract class Calculation {

	public static Calculation sum(Object o1, Object o2) {
		return null;
	}

	public static Calculation multiply(Object o1, Object o2) {
		return null;
	}

	public abstract boolean isConstant();

	public abstract int getValue();

}
