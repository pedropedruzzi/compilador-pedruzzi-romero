package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Variable extends AssemblerSymbol {

	protected final Type type;

	public Variable(String id, String asmSymbol, Type type) {
		super(id, asmSymbol);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
