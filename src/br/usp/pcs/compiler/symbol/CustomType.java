package br.usp.pcs.compiler.symbol;

import br.usp.pcs.compiler.symbol.type.Type;

public class CustomType extends Symbol {
	
	protected Type definition;

	public CustomType(String id, Type definition) {
		super(id);
		this.definition = definition;
	}
	
	public Type getDefinition() {
		return definition;
	}

}
