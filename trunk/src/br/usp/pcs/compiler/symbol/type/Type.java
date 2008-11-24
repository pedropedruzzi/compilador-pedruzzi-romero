package br.usp.pcs.compiler.symbol.type;

public abstract class Type {

	public static final int UNKOWN_SIZE = -1;

	public abstract int sizeOf();

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
	
}
