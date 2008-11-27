package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class Function extends AssemblerSymbol {
	
	private Type returnType;
	private Type[] argumentType;

	public Function(String id, String asmSymbol, Type returnType, Type ... argumentType) {
		super(id, asmSymbol);
		this.returnType = returnType;
		this.argumentType = argumentType;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public Type[] getArgumentType() {
		return argumentType;
	}
	
}
