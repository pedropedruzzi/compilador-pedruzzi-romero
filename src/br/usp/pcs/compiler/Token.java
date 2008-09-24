package br.usp.pcs.compiler;

public class Token {

	TokenType type;
	Object value;
	
	public Token() {
		
	}
	
	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
		
	}
	
	public enum TokenType { IDENTIFIER, OPERATOR, STRING_LITERAL, CHAR_LITERAL }
	
	@Override
	public String toString() {
		return type.name() + "\t" + value;
	}
}
