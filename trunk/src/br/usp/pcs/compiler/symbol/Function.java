package br.usp.pcs.compiler.entity;

public class Function extends AddressableEntity {
	
	private Variable result;
	private Variable[] arguments;

	public Function(String address, Variable result, Variable[] arguments) {
		super(address);
		this.result = result;
		this.arguments = arguments;
	}
	
	public Variable getResult() {
		return result;
	}
	
	public Variable[] getArguments() {
		return arguments;
	}
	
}
