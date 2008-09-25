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
	
	private static final String SINGLE_OP = "{}[](),;~?:";
	private static final String OPS = "{}[]()+-*/%.,;<>=&|~!^?:#";
	private static final Set<String> OPERATORS = new HashSet<String>(Arrays
			.asList("->", "++", "--", "<<", ">>", "<=", ">=", "==", "!=", "&&",
					"||", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=", "&=",
					"^=", "|=", "##", "..."));
	
	
	
	private Reader input;
	private StringBuffer buffer = new StringBuffer(256);
	private LexicalState state = LexicalState.INITIAL;
	private int lin = 1;
	private int col = 0;
	private int tokenLin;
	private int tokenCol;

	public LexicalParser(String filename) throws FileNotFoundException {
		input = new FileReader(filename);
	}
	
	public Token nextToken() {
		Token token = null;
		boolean escaped = false;
		
		char opTmpChar = '\0';
		
		while (token == null) {
			if (isEOF()) return null;
			char c = nextChar();
			
			// TODO:
			// preprocessamento (# e ##)
			// constantes numericas
			// comentarios
			
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
						case '?':
							type = TokenType.QUESTION_MARK;
							break;
						case ':':
							type = TokenType.COLON;
							break;
						}
						if (type == null) error("assertion error! c = " + c);
						token = singleToken(type);
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
				case '-':
					if (c == '>') type = TokenType.ARROW;
					else if (c == '-') type = TokenType.DECREMENT;
					else if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_MINUS;
					else {
						type = TokenType.MINUS;
						giveBack(c);
					}
					break;
				case '+':
					if (c == '+') type = TokenType.INCREMENT;
					else if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_PLUS;
					else {
						type = TokenType.PLUS;
						giveBack(c);
					}
					break;
				case '&':
					if (c == '&') type = TokenType.LOGICAL_AND;
					else if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_AND;
					else {
						type = TokenType.BITWISE_AND;
						giveBack(c);
					}
					break;
				case '|':
					if (c == '|') type = TokenType.LOGICAL_OR;
					else if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_OR;
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
				case '*':
					if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_MULTIPLICATION;
					else {
						type = TokenType.MULTIPLICATION;
						giveBack(c);
					}
					break;
				case '/':
					if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_DIVISION;
					else if (c == '/') {
						state = LexicalState.LINE_COMMENT;
					} else if (c == '*') {
						state = LexicalState.COMMENT;
					}
					else {
						type = TokenType.DIVISION;
						giveBack(c);
					}
					break;
				case '%':
					if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_MODULUS;
					else {
						type = TokenType.MODULUS;
						giveBack(c);
					}
					break;
				case '^':
					if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_NOT;
					else {
						type = TokenType.BITWISE_XOR;
						giveBack(c);
					}
					break;
				case '>':
					if (c == '=') type = TokenType.GREATER_OR_EQUAL;
					else if (c != '>') {
						type = TokenType.GREATER;
						giveBack(c);
					}
					break;
				case '<':
					if (c == '=') type = TokenType.LESS_OR_EQUAL;
					else if (c != '<') {
						type = TokenType.LESS;
						giveBack(c);
					}
					break;
				case '.':
					if (c != '.') {
						type = TokenType.DOT;
						giveBack(c);
					}
				}

				if (type != null) {
					// fechou um token!
					token = singleToken(type);
					state = LexicalState.INITIAL;
				} else if (state == LexicalState.OPERATOR1) {
					state = LexicalState.OPERATOR2;
				}
			}
			break;

			case OPERATOR2:
			{
				TokenType type = null;
				switch (opTmpChar) {
					case '>':
						if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_SHIFT_RIGHT;
						else {
							type = TokenType.SHIFT_RIGHT;
							giveBack(c);
						}
						break;
					case '<':
						if (c == '=') type = TokenType.COMPOUND_ASSIGNMENT_SHIFT_LEFT;
						else {
							type = TokenType.SHIFT_LEFT;
							giveBack(c);
						}
						break;
					case '.':
						if (c == '.') type = TokenType.ELLIPSIS;
						else error("invalid token");
						break;
				}
				
				if (type != null) {
					token = singleToken(type);
					state = LexicalState.INITIAL;
				} else {
					error("assertion error!");
				}
			}
			break;
				
			case IDENTIFIER:
				if (isIdentifierPart(c)) {
					buffer.append(c);
				} else {
					String identifier = buffer.toString();
					if (TokenType.isKeyword(identifier))
						token = singleToken(TokenType.getKeyword(identifier));
					else
						token = identifierToken(identifier);
					
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
							token = stringLiteralToken(buffer.toString()); 
							
							

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
							token = charLiteralToken(ch.charAt(0));
							
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
				
			case COMMENT:
				if (c == '*') state = LexicalState.COMMENT2;
				break;
				
			case COMMENT2:
				if (c == '/') state = LexicalState.INITIAL;
				else state = LexicalState.LINE_COMMENT;
				break;

			case LINE_COMMENT:
				if (c == 0x0a || c == 0x0d) state = LexicalState.INITIAL; 
			}
		}
		
		return token;
	}

	/*
	 * BLABLABLA
	 * 
	 */
	private void error(String text) {
		throw new RuntimeException("Lex error on line " + tokenLin + " column " + tokenCol + ": " + text);
	}

	private Token identifierToken(String name) {
		Token t = new Token(TokenType.IDENTIFIER, name);
		t.lin = tokenLin;
		t.col = tokenCol;
		return t;
	}

	private Token stringLiteralToken(String string) {
		Token t = new Token(TokenType.STRING_LITERAL, string);
		t.lin = tokenLin;
		t.col = tokenCol;
		return t;
	}

	private Token charLiteralToken(char c) {
		Token t = new Token(TokenType.CHAR_LITERAL, c);
		t.lin = tokenLin;
		t.col = tokenCol;
		return t;
	}

	private boolean isIdentifierPart(Character c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}
	
	private Token singleToken(TokenType type) {
		Token t = new Token(type);
		t.lin = tokenLin;
		t.col = tokenCol;
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
