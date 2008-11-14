package br.usp.pcs.compiler;

public interface Lex {

	public Token nextToken();
	public void giveBack(Token t);
	public boolean hasToken();
	
}
