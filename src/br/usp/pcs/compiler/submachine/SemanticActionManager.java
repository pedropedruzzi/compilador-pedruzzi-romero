package br.usp.pcs.compiler.submachine;

import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;


public interface SemanticActionManager {

	public void processTransition(String subMachineId, int state, TokenType tokenType, Token token);

}
