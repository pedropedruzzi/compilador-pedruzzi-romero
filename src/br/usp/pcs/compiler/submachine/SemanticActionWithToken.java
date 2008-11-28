package br.usp.pcs.compiler.submachine;

import br.usp.pcs.compiler.Token;

public abstract class SemanticActionWithToken implements SemanticAction {

	public void doAction(Object result) {
		doAction((Token) result);
	}
	
	public abstract void doAction(Token token);

}
