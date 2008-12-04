package br.usp.pcs.compiler.entity;

import br.usp.pcs.compiler.entity.type.Type;

public class Function extends AddressableEntity {
	
	private Type returnType;
	private Variable[] arguments;
	private boolean isDefined;

	public Function(String address, Type returnType, Variable[] arguments) {
		super(address);
		this.returnType = returnType;
		this.arguments = arguments;
	}
	
	public Type getReturnType() {
		return returnType;
	}
	
	public Variable[] getArguments() {
		return arguments;
	}
	
	public void markAsDefined() {
		isDefined = true;
	}
	
	public boolean isDefined() {
		return isDefined;
	}
	
}
