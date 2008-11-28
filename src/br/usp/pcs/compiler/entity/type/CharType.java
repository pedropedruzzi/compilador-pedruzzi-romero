package br.usp.pcs.compiler.entity.type;

public class CharType extends PrimitiveType {
	
	private static final CharType SINGLETON = new CharType();
	
	private CharType() {
		
	}
	
	static CharType instance() {
		return SINGLETON;
	}

	public String toString() {
		return "char";
	}
	
}
