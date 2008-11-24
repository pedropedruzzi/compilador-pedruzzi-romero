package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Variable extends Symbol {

	protected final Type type;
	protected int address;
	

	public Variable(String id, Type type, int address) {
		super(id);
		this.type = type;
		this.address = address;
	}

}
