package br.usp.pcs.compiler.symbol.type;

public class SizedArray extends Array {
	
	private final int length;
	private int size = Type.UNKOWN_SIZE;

	public SizedArray(Type innerType, int length) {
		super(innerType);
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public int sizeOf() {
		if (size == Type.UNKOWN_SIZE) {
			size = length * innerType.sizeOf();
		}
		return size;
	}

}
