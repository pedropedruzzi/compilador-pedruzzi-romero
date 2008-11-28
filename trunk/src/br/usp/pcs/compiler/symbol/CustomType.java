package br.usp.pcs.compiler.entity;

import br.usp.pcs.compiler.entity.type.Type;

public class CustomType extends Entity {
	
	protected Type definition;

	public CustomType(Type definition) {
		this.definition = definition;
	}
	
	public Type getDefinition() {
		return definition;
	}

}
