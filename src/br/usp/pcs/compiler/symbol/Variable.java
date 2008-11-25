package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Variable extends Symbol {

	protected final Type type;
	protected final byte[] initial;

	public Variable(String id, Type type) {
		this(id, type, null);
	}

	public Variable(String id, Type type, byte[] initial) {
		super(id);
		this.type = type;
		this.initial = initial;
	}

}
