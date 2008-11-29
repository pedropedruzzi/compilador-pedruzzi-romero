package br.usp.pcs.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import br.usp.pcs.compiler.Token.TokenType;

public class LexicalParser implements Lex {
	
	private static final String SINGLE_OP = "{}[](),;~-+*%^.";
	private static final String OPS = "{}[]()+-*/%.,;<>=&|~!^?:#";
	
	private Reader input;
	private StringBuffer buffer = new StringBuffer(256);
	private LexicalState state = LexicalState.INITIAL;
	private int lin = 1;
	private int col = 0;
	private int tokenLin;
	private int tokenCol;
	
	private Token last;

	public LexicalParser(String filename) throws FileNotFoundException {
		input = new FileReader(filename);
	}
	
	public void giveBack(Token token) {
		if (last != null) throw new RuntimeException("Can't give more than one token back.");
		last = token;
	}
	
	public Token nextToken() {
		if (last == null) parseNextToken();
		Token token = last;
		last = null;
		return token;
	}
	
	public boolean hasToken() {
		if (last == null) parseNextToken();
		return last != null;
	}
	
	private void parseNextToken() {
		boolean escaped = false;
		
		char opTmpChar = '\0';
		
		while (last == null) {
			if (isEOF()) return;
			char c = nextChar();
			
			switch (state) {
			case INITIAL:
				if (!Character.isWhitespace(c)) {
					tokenLin = lin;
					tokenCol = col;
					if (isSingleCharOperator(c)) {
						TokenType type = null;
						switch (c) {
						case '{':
							type = TokenType.BRACE_OPEN;
							break;
						case '}':
							type = TokenType.BRACE_CLOSE;
							break;
						case '[':
							type = TokenType.SUBSCRIPT_OPEN;
							break;
						case ']':
							type = TokenType.SUBSCRIPT_CLOSE;
							break;
						case '(':
							type = TokenType.PARENTHESES_OPEN;
							break;
						case ')':
							type = TokenType.PARENTHESES_CLOSE;
							break;
						case ',':
							type = TokenType.COMMA;
							break;
						case ';':
							type = TokenType.SEMICOLON;
							break;
						case '~':
							type = TokenType.TILDE;
							break;
						case '-':
							type = TokenType.MINUS;
							break;
						case '+':
							type = TokenType.PLUS;
							break;
						case '*':
							type = TokenType.MULTIPLICATION;
							break;
						case '%':
							type = TokenType.MODULUS;
							break;
						case '^':
							type = TokenType.BITWISE_XOR;
							break;
						case '.':
							type = TokenType.DOT;
							break;
						}

						if (type == null) error("assertment error! c = " + c);
						last = singleToken(type);
					} else if (isOperatorChar(c)) {
						state = LexicalState.OPERATOR1;
						opTmpChar = c;
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
						error("unknown token: " + buffer.toString());
					}
				}
				break;
				
			case OPERATOR1:
			{
				TokenType type = null;
				switch (opTmpChar) {
				case '&':
					if (c == '&') type = TokenType.LOGICAL_AND;
					else {
						type = TokenType.BITWISE_AND;
						giveBack(c);
					}
					break;
				case '|':
					if (c == '|') type = TokenType.LOGICAL_OR;
					else {
						type = TokenType.BITWISE_OR;
						giveBack(c);
					}
					break;
				case '!':
					if (c == '=') type = TokenType.NOT_EQUAL;
					else {
						type = TokenType.LOGICAL_NOT;
						giveBack(c);
					}
					break;
				case '=':
					if (c == '=') type = TokenType.EQUAL;
					else {
						type = TokenType.ASSIGNMENT;
						giveBack(c);
					}
					break;
				case '/':
					if (c == '/') {
						state = LexicalState.LINE_COMMENT;
					} else if (c == '*') {
						state = LexicalState.COMMENT;
					} else {
						type = TokenType.DIVISION;
						giveBack(c);
					}
					break;
				case '>':
					if (c == '=') type = TokenType.GREATER_OR_EQUAL;
					else if (c == '>') type = TokenType.SHIFT_RIGHT;
					else {
						type = TokenType.GREATER;
						giveBack(c);
					}
					break;
				case '<':
					if (c == '=') type = TokenType.LESS_OR_EQUAL;
					else if (c == '<') type = TokenType.SHIFT_LEFT;
					else {
						type = TokenType.LESS;
						giveBack(c);
					}
					break;
				}
				
				if (type != null) {
					last = singleToken(type);
					state = LexicalState.INITIAL;
				}
			}
			break;
				
			case IDENTIFIER:
				if (isIdentifierPart(c)) {
					buffer.append(c);
				} else {
					String identifier = buffer.toString();
					if (TokenType.isKeyword(identifier))
						last = singleToken(TokenType.getKeyword(identifier));
					else
						last = identifierToken(identifier);
					
					buffer.setLength(0);
					state = LexicalState.INITIAL;
					giveBack(c);
				}
				break;
				
			case STRING_LITERAL:
				if (escaped) {
					// TODO suportar escape sequences
					buffer.append(c);
					escaped = false;
				} else {
					if (c == '\\') {
						escaped = true;
					} else {
						if (c != '"') {
							buffer.append(c);
						} else {
							last = stringLiteralToken(buffer.toString()); 
							
							

							buffer.setLength(0);
							state = LexicalState.INITIAL;
						}
					}
				}
				break;

			case CHAR_CONSTANT:
				if (escaped) {
					// TODO suportar escape sequences
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
								error("invalid character constant: " + ch);
							}
							last = charLiteralToken(ch.charAt(0));
							
							buffer.setLength(0);
							state = LexicalState.INITIAL;
						}
					}
				}
				break;
				
			case NUMERIC_CONSTANT:
				if (Character.isDigit(c)) {
					buffer.append(c);
				} else {
					last = integerLiteralToken(Integer.parseInt(buffer.toString()));

					buffer.setLength(0);
					state = LexicalState.INITIAL;
					giveBack(c);
				}
				break;
				
			case COMMENT:
				if (c == '*') state = LexicalState.COMMENT2;
				break;
				
			case COMMENT2:
				if (c == '/') state = LexicalState.INITIAL;
				else state = LexicalState.COMMENT;
				break;

			case LINE_COMMENT:
				if (c == 0x0a || c == 0x0d) state = LexicalState.INITIAL; 
			}
		}
	}
	
	private void error(String text) {
		throw new RuntimeException("Lex error on line " + tokenLin + " column " + tokenCol + ": " + text);
	}

	private Token identifierToken(String name) {
		Token t = new Token(TokenType.IDENTIFIER, name);
		t.setLin(tokenLin);
		t.setCol(tokenCol);
		return t;
	}

	private Token stringLiteralToken(String string) {
		Token t = new Token(TokenType.STRING_LITERAL, string);
		t.setLin(tokenLin);
		t.setCol(tokenCol);
		return t;
	}

	private Token charLiteralToken(char c) {
		Token t = new Token(TokenType.CHAR_LITERAL, c);
		t.setLin(tokenLin);
		t.setCol(tokenCol);
		return t;
	}

	private Token integerLiteralToken(int i) {
		Token t = new Token(TokenType.INTEGER_LITERAL, i);
		t.setLin(tokenLin);
		t.setCol(tokenCol);
		return t;
	}

	private boolean isIdentifierPart(Character c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}
	
	private Token singleToken(TokenType type) {
		Token t = new Token(type);
		t.setLin(tokenLin);
		t.setCol(tokenCol);
		return t;
	}

	// reading API
	private Integer intChar = null;
	private boolean wasCr = false;
	private char nextChar() {
		if (intChar == null) intChar = readChar();
		
		char c = (char) intChar.intValue();
		intChar = null;
		return c;
	}
	private void giveBack(char c) {
		intChar = (int) c;
	}
	
	private int readChar() {
		try {
			int c = input.read();
			if (c == 0x0d) {
				col = 0;
				lin++;
				wasCr = true;
			} else if (c == 0x0a) {
				if (!wasCr) {
					col = 0;
					lin++;
				}
				wasCr = false;
			} else {
				wasCr = false;
				col++;
			}
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("error reading source");
		}
	}

	private boolean isEOF() {
		if (intChar == null) intChar = readChar();
		return intChar == -1;
	}

	private boolean isIdentifierStart(char c) {
		return Character.isLetter(c) || c == '_';
	}

	private boolean isOperatorChar(char c) {
		return OPS.indexOf((int) c) >= 0;
	}
	
	private boolean isSingleCharOperator(char c) {
		return SINGLE_OP.indexOf((int) c) >= 0;
	}

	public static void main(String[] args) throws FileNotFoundException {
		LexicalParser lex = new LexicalParser("src/br/usp/pcs/compiler/LexicalParser.java");

		for (Token token = lex.nextToken(); token != null; token = lex.nextToken()) {
			System.out.println(token);
		}
	}
	
	enum LexicalState { 
		INITIAL, OPERATOR1, END, IDENTIFIER, STRING_LITERAL, 
		CHAR_CONSTANT, NUMERIC_CONSTANT, OPERATOR2, LINE_COMMENT, 
		COMMENT, COMMENT2
	}
	
}
