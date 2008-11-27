package br.usp.pcs.compiler.symbol;

public abstract class AssemblerSymbol extends Symbol {
	
	protected String asmSymbol;
	
	public AssemblerSymbol(String id, String asmSymbol) {
		super(id);
		this.asmSymbol = asmSymbol;
	}
	
	public String getAsmSymbol() {
		return asmSymbol;
	}
	
}
