package br.usp.pcs.compiler.scope;

import java.util.HashMap;
import java.util.Map;

import br.usp.pcs.compiler.entity.CustomType;
import br.usp.pcs.compiler.entity.Entity;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;

public class LocalScope implements Scope {
	
	private Scope parent;
	private GlobalScope global;
	private Map<String, Variable> variables = new HashMap<String, Variable>();
	
	public LocalScope(Scope parent) {
		this.parent = parent;
		if (parent instanceof LocalScope) global = ((LocalScope) parent).getGlobal();
		else if (parent instanceof GlobalScope) global = (GlobalScope) parent;
		else throw new IllegalArgumentException("unexpected SymbolTable: " + parent.getClass().getName());
	}
	
	public Scope getParent() {
		return parent;
	}
	
	private GlobalScope getGlobal() {
		return global;
	}
	
	public boolean containsSymbol(String symbol) {
		return containsVariableCurrentScope(symbol) || parent.containsSymbol(symbol);
	}
	
	public void registerSymbol(String symbol, Entity e) {
		if (e instanceof Variable) registerVariable(symbol, (Variable) e);
		else throw new IllegalArgumentException("unsupported entity: " + e.getClass().getName());
	}

	public void dump() {
		for (String symbol : variables.keySet()) {
			System.out.println(symbol + ": " + variables.get(symbol).getClass().getSimpleName());
		}
	}

	public boolean containsCustomType(String symbol) {
		return global.containsCustomType(symbol);
	}

	public boolean containsFunction(String symbol) {
		return global.containsFunction(symbol);
	}

	public boolean containsVariable(String symbol) {
		return variables.containsKey(symbol) || parent.containsVariable(symbol);
	}

	public boolean containsVariableCurrentScope(String symbol) {
		return variables.containsKey(symbol);
	}

	public CustomType retrieveCustomType(String symbol) {
		return global.retrieveCustomType(symbol);
	}

	public Function retrieveFunction(String symbol) {
		return global.retrieveFunction(symbol);
	}

	public Variable retrieveVariable(String symbol) {
		Variable v = variables.get(symbol);
		if (v == null) v = parent.retrieveVariable(symbol);
		return v;
	}

	public void registerCustomType(String symbol, CustomType t) {
		throw new UnsupportedOperationException();
	}

	public void registerFunction(String symbol, Function f) {
		throw new UnsupportedOperationException();
	}

	public void registerVariable(String symbol, Variable v) {
		if (containsVariableCurrentScope(symbol)) throw new RuntimeException("duplicated local variable: " + symbol);
		variables.put(symbol, v);
	}
	
}
