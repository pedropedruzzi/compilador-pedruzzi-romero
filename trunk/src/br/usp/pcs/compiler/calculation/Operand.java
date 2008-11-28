package br.usp.pcs.compiler.calculation;

import java.util.ArrayList;
import java.util.List;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;

public class Operand {
	
	private List<TokenType> op1 = new ArrayList<TokenType>();

	public void unaryOperator(Token t) {
		if (t.getType().isOperand())
			throw new CompilationException("shit!");
		op1.add(t.getType());
	}
	
	

}
