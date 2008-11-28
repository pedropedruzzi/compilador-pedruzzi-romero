package br.usp.pcs.compiler.entity.type;

public class Field {
	
	private final String id;
	private final Type type;

	public Field(String id, Type type) {
		this.id = id;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public Type getType() {
		return type;
	}

}
