package br.usp.pcs.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.usp.pcs.compiler.Token.TokenType;

public class LexicalParser {

	private static final Set<String> KEYWORDS = new HashSet<String>(Arrays
			.asList("auto", "break", "case", "char", "const", "continue",
					"default", "do", "double", "else", "enum", "extern",
					"float", "for", "goto", "if", "int", "long", "register",
					"return", "short", "signed", "sizeof", "static", "struct",
					"switch", "typedef", "union", "unsigned", "void",
					"volatile", "while"));
	
	private static final char[] PONCTUATION_OLD = {'{', '}', '[', ']', '(', ')',
			'+', '-', '*', '/', '%', '.', ',', ';', '<', '>', '=', '&', '|',
			'~', '!', '^', '?', ':', '#', '\'', '"'};
	
	private static final String PONCTUATION = "{}[]()+-*/%.,;<>=&|~!^?:#'\"";
	private static final String OPS = "{}[]()+-*/%.,;<>=&|~!^?:#";
	private static final Set<String> OPERATORS = new HashSet<String>(Arrays
			.asList("->", "++", "--", "<<", ">>", "<=", ">=", "==", "!=", "&&",
					"||", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", "&=",
					"^=", "|=", "##", "..."));
	
	
	
	private Reader input;
	private StringBuffer buffer = new StringBuffer(256);
	private LexicalState state = LexicalState.INITIAL;

	public LexicalParser(String filename) throws FileNotFoundException {
		input = new FileReader(filename);
	}
	
	public Token nextToken() {
		Token token = null;
		boolean escaped = false;
		
		while (token == null) {
			if (isEOF()) return null;
			char c = nextChar();
			
			// TODO:
			// preprocessamento
			// constantes numericas
			// strings
			// comentarios
			
			switch (state) {
			case INITIAL:
				if (!Character.isWhitespace(c)) {
					if (isOperatorChar(c)) {
						state = LexicalState.OPERATOR;
						buffer.append(c);
					} else if (isIdentifierStart(c)) {
						state = LexicalState.IDENTIFIER;
						buffer.append(c);
					} else if (Character.isDigit(c)) {
						state = LexicalState.NUMERIC_CONSTANT;
						buffer.append(c);
					} else if (c == '"') {
						state = LexicalState.STRING_LITERAL;
					} else if (c == '\'') {
						state = LexicalState.CHAR_CONSTANT;
					} else {
						// TODO: lex parser error!
						throw new IllegalStateException("lex parser error: unknown token: " + buffer.toString());
					}
				}
				break;
				
			case OPERATOR:
				if (isOperatorChar(c)) {
					buffer.append(c);
				} else {
					String operator = buffer.toString();
					token = operatorToken(operator);
					if (token == null) {
						throw new IllegalStateException("lex parser error: invalid operator");
					}
					buffer.setLength(0);
					state = LexicalState.INITIAL;
					giveBack(c);
				}
				break;
				
			case IDENTIFIER:
				if (isIdentifierPart(c)) {
					buffer.append(c);
				} else {
					String identifier = buffer.toString();
					token = identifierToken(identifier);
					
					buffer.setLength(0);
					state = LexicalState.INITIAL;
					giveBack(c);
				}
				break;
				
			case STRING_LITERAL:
				if (escaped) {
					buffer.append(c);
					escaped = false;
				} else {
					if (c == '\\') {
						escaped = true;
					} else {
						if (c != '"') {
							buffer.append(c);
						} else {
							token = new Token(TokenType.STRING_LITERAL, buffer.toString());

							buffer.setLength(0);
							state = LexicalState.INITIAL;
						}
					}
				}
				break;

			case CHAR_CONSTANT:
				if (escaped) {
					buffer.append(c);
					escaped = false;
				} else {
					if (c == '\\') {
						escaped = true;
					} else {
						if (c != '\'') {
							buffer.append(c);
						} else {
							String ch = buffer.toString();
							if (ch.length() != 1) {
								lexicalException("invalid character constant: " + ch);
							}
							token = new Token(TokenType.CHAR_LITERAL, ch.charAt(0));
							
							buffer.setLength(0);
							state = LexicalState.INITIAL;
						}
					}
				}
				break;
				
			case NUMERIC_CONSTANT:
				// TODO
				buffer.setLength(0);
				state = LexicalState.INITIAL;
				break;
			}
		}
		
		return token;
	}
	
	private void lexicalException(String string) {
		throw new RuntimeException(string);
	}

	private Token identifierToken(String name) {
		return new Token(TokenType.IDENTIFIER, name);
	}

	private boolean isIdentifierPart(Character c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}

	private Token operatorToken(String operator) {
		// TODO: um tipo pra cada um!
		return new Token(TokenType.OPERATOR, operator);
	}

	// reading API
	private Integer intChar = null;
	private char nextChar() {
		if (intChar == null) {
			try {
				intChar = input.read();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("error reading source");
			}
		}
		
		char c = (char) intChar.intValue();
		intChar = null;
		return c;
	}
	private void giveBack(char c) {
		intChar = (int) c;
	}

	private boolean isEOF() {
		if (intChar == null) {
			try {
				intChar = input.read();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("error reading source");
			}
		}
		return intChar == -1;
	}

	private boolean isIdentifierStart(char c) {
		return Character.isLetter(c) || c == '_';
	}

	private boolean isOperatorChar(char c) {
		return OPS.indexOf((int) c) >= 0;
	}

	public static void main(String[] args) throws FileNotFoundException {
		LexicalParser lex = new LexicalParser("src/br/usp/pcs/compiler/LexicalParser.java");

		for (Token token = lex.nextToken(); token != null; token = lex.nextToken()) {
			System.out.println(token);
		}
	}
	
	enum LexicalState { INITIAL, OPERATOR, END, IDENTIFIER, STRING_LITERAL, CHAR_CONSTANT, NUMERIC_CONSTANT }
	
}
