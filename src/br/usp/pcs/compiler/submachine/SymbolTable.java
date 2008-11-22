package br.usp.pcs.compiler.submachine;

import java.util.Map;

import br.usp.pcs.compiler.symbol.Symbol;

public class SymbolTable {
	
	private SymbolTable parent;
	private Map<String, Symbol> table;
	
	public SymbolTable() {
		
	}
	
	public SymbolTable(SymbolTable parent) {
		this.parent = parent;
	}
	
	public boolean containsSymbol(String id) {
		return table.containsKey(id) || (parent != null && parent.containsSymbol(id));
	}
	
	public Symbol getSymbol(String id) {
		Symbol s = table.get(id);
		if (s == null && parent != null) s = parent.getSymbol(id);
		return s;
	}
	
	public void addSymbol(Symbol s) {
		if (containsSymbol(s.getId())) throw new RuntimeException("symbol identifier already is in use: " + s.getId());
		table.put(s.getId(), s);
	}
	
}
