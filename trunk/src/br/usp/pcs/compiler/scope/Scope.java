package br.usp.pcs.compiler.scope;

import br.usp.pcs.compiler.entity.CustomType;
import br.usp.pcs.compiler.entity.Entity;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;

public interface Scope {
	
	public Scope getParent();
	public boolean isGlobal();
	
	public boolean containsSymbol(String symbol);
	public void registerSymbol(String symbol, Entity e);
	
	public boolean containsVariable(String symbol);
	public boolean containsVariableCurrentScope(String symbol);
	public Variable retrieveVariable(String symbol);
	public void registerVariable(String symbol, Variable v);
	
	public boolean containsFunction(String symbol);
	public Function retrieveFunction(String symbol);
	public void registerFunction(String symbol, Function f);
	
	public boolean containsCustomType(String symbol);
	public CustomType retrieveCustomType(String symbol);
	public void registerCustomType(String symbol, CustomType t);

	public void dump();
	
}
