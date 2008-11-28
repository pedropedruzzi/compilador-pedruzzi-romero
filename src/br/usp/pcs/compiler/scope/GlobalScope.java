package br.usp.pcs.compiler.scope;

import java.util.HashMap;
import java.util.Map;

import br.usp.pcs.compiler.entity.CustomType;
import br.usp.pcs.compiler.entity.Entity;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;

public class GlobalScope implements Scope {
	
	private Map<String, Variable> variables = new HashMap<String, Variable>();
	private Map<String, Function> functions = new HashMap<String, Function>();
	private Map<String, CustomType> customTypes = new HashMap<String, CustomType>();
	
	public GlobalScope getParent() {
		return null;
	}
	
	public boolean containsSymbol(String symbol) {
		return containsVariable(symbol) || containsCustomType(symbol) || containsFunction(symbol);
	}
	
	public void registerSymbol(String symbol, Entity e) {
		if (e instanceof Variable) registerVariable(symbol, (Variable) e);
		else if (e instanceof Function) registerFunction(symbol, (Function) e);
		else if (e instanceof CustomType) registerCustomType(symbol, (CustomType) e);
		else throw new IllegalArgumentException("unsupported entity: " + e.getClass().getName());
	}

	public void dump() {
		for (String symbol : variables.keySet()) {
			System.out.println(symbol + ": " + variables.get(symbol).getClass().getSimpleName());
		}
	}

	public boolean containsCustomType(String symbol) {
		return customTypes.containsKey(symbol);
	}

	public boolean containsFunction(String symbol) {
		return functions.containsKey(symbol);
	}

	public boolean containsVariable(String symbol) {
		return variables.containsKey(symbol);
	}

	public boolean containsVariableCurrentScope(String symbol) {
		return containsVariable(symbol);
	}

	public CustomType retrieveCustomType(String symbol) {
		return customTypes.get(symbol);
	}

	public Function retrieveFunction(String symbol) {
		return functions.get(symbol);
	}

	public Variable retrieveVariable(String symbol) {
		return variables.get(symbol);
	}

	public void registerCustomType(String symbol, CustomType t) {
		if (containsCustomType(symbol)) throw new RuntimeException("duplicated type definition: " + symbol);
		customTypes.put(symbol, t);
	}

	public void registerFunction(String symbol, Function f) {
		if (containsFunction(symbol)) throw new RuntimeException("duplicated funcion definition: " + symbol);
		functions.put(symbol, f);
	}

	public void registerVariable(String symbol, Variable v) {
		if (containsVariable(symbol)) throw new RuntimeException("duplicated global variable: " + symbol);
		variables.put(symbol, v);
	}
	
}
