package br.usp.pcs.compiler.entity;

import br.usp.pcs.compiler.entity.type.Type;

public class Variable extends AddressableEntity {

	protected final Type type;

	public Variable(String address, Type type) {
		super(address);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
