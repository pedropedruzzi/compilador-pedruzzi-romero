package br.usp.pcs.compiler;

import java.util.HashSet;
import java.util.Set;

public class Token {

	private TokenType type;
	private Object value;
	private int lin;
	private int col;
	
	public Token() {
		
	}
	
	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
		
	}
	
	public Token(TokenType type) {
		this.type = type;
	}

	public enum TokenType {
		IDENTIFIER("id"), STRING_LITERAL, CHAR_LITERAL, INTEGER_LITERAL("num"),
		
		LOGICAL_OR("||"), LOGICAL_AND("&&"),
		NOT_EQUAL("!="), EQUAL("=="),
		GREATER_OR_EQUAL(">="),
		LESS_OR_EQUAL("<="),
		MINUS("-"), PLUS("+"), BITWISE_AND("&"), BITWISE_OR("|"),
		LOGICAL_NOT("!"), ASSIGNMENT("="), MULTIPLICATION("*"), SHIFT_LEFT("<<"), LESS("<"), SHIFT_RIGHT(">>"),
		GREATER(">"), BITWISE_XOR("^"), MODULUS("%"), DIVISION("/"), BRACE_OPEN("{"), BRACE_CLOSE("}"),
		SUBSCRIPT_OPEN("["), SUBSCRIPT_CLOSE("]"), PARENTHESES_OPEN("("), PARENTHESES_CLOSE(")"),
		COMMA(","), SEMICOLON(";"), TILDE("~"), DOT("."),
		
		CHAR(true), CONST(true), CONTINUE(true),
		ELSE(true),
		FOR(true), IF(true), INT(true), 
		RETURN(true), TYPE(true),
		VOID(true),
		WHILE(true);
		
		private static Set<String> keywords;
		private String id;
		
		TokenType() {
			this(false);
		}
		
		TokenType(String id) {
			this.id = id;
		}
		
		TokenType(boolean keyword) {
			this.id = name().toLowerCase();
			if (keyword) addKeyword(id);
		}
		
		private void addKeyword(String keyword) {
			if (keywords == null) keywords = new HashSet<String>();
			keywords.add(keyword);
		}

		public static boolean isKeyword(String name) {
			return keywords.contains(name);
		}

		public static TokenType getKeyword(String name) {
			return TokenType.valueOf(name.toUpperCase());
		}
		
		public static TokenType getById(String id) {
			for (TokenType tt : values()) {
				if (id.equals(tt.id)) return tt;
			}
			
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Integer.toString(lin) + ":" + Integer.toString(col) + "\t" + type.name() + (value != null ? "\t" + value : "");
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getLin() {
		return lin;
	}

	public void setLin(int lin) {
		this.lin = lin;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
}
