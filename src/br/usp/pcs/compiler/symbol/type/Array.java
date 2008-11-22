package br.usp.pcs.compiler.symbol.type;

public class Array extends Type {
	
	protected final Type innerType;

	public Array(Type innerType) {
		this.innerType = innerType;
	}
	
	public Type getInnerType() {
		return innerType;
	}
	
	public int sizeOf() {
		throw new RuntimeException("unkown array size!");
	}

}
