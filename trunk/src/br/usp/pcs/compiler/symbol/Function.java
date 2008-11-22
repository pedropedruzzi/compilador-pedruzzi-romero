package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Function extends Symbol {
	
	private Type returnType;
	private Type[] argumentType;

	public Function(String id, Type returnType, Type ... argumentType) {
		super(id);
		this.returnType = returnType;
		this.argumentType = argumentType;
	}
	
}
