package br.usp.pcs.compiler.entity.type;

public abstract class PrimitiveType extends Type {
	
	public int sizeOf() {
		return 2;
	}
	
	public static PrimitiveType intTypeInstance() {
		return IntType.instance();
	}
	
	public static PrimitiveType charTypeInstance() {
		return CharType.instance();
	}
	
}
