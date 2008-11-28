package br.usp.pcs.compiler.entity.type;

public class Pointer extends Type {
	
	protected final Type innerType;

	public Pointer(Type innerType) {
		this.innerType = innerType;
	}
	
	public Type getInnerType() {
		return innerType;
	}

	public int sizeOf() {
		return 2;
	}

	public String toString() {
		return "*" + innerType.toString();
	}
	
}
