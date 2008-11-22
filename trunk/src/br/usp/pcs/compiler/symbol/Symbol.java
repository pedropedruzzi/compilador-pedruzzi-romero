package br.usp.pcs.compiler.symbol;

public abstract class Symbol {
	
	protected String id;
	
	public Symbol(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
}
