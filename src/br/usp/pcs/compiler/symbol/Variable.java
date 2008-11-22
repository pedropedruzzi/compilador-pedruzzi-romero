package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Variable extends Symbol {

	private final Type type;

	public Variable(String id, Type type) {
		super(id);
		this.type = type;
	}

}
