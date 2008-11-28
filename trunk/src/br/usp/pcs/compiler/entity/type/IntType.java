package br.usp.pcs.compiler.entity.type;

public class IntType extends PrimitiveType {
	
	private static final IntType SINGLETON = new IntType();
	
	private IntType() {
		
	}
	
	static IntType instance() {
		return SINGLETON;
	}

	public String toString() {
		return "int";
	}
	
}
