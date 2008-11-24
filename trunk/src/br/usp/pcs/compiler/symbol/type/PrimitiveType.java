package br.usp.pcs.compiler.symbol.type;

public abstract class PrimitiveType extends Type {
	
	private static final PrimitiveType INT_TYPE = new IntType();
	private static final PrimitiveType CHAR_TYPE = new CharType();

	public int sizeOf() {
		return 2;
	}
	
	public static PrimitiveType intTypeInstance() {
		return INT_TYPE;
	}
	
	public static PrimitiveType charTypeInstance() {
		return CHAR_TYPE;
	}
	
}
