package br.usp.pcs.compiler;

import java.util.HashSet;
import java.util.Set;

public class Token {

	TokenType type;
	Object value;
	int lin;
	int col;
	
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
		IDENTIFIER, STRING_LITERAL, CHAR_LITERAL,
		
		ARROW, DECREMENT, COMPOUND_ASSIGNMENT_MINUS,
		COMPOUND_ASSIGNMENT_PLUS, INCREMENT, LOGICAL_OR, LOGICAL_AND,
		COMPOUND_ASSIGNMENT_AND, COMPOUND_ASSIGNMENT_OR, NOT_EQUAL, EQUAL,
		COMPOUND_ASSIGNMENT_MULTIPLICATION, COMPOUND_ASSIGNMENT_DIVISION,
		COMPOUND_ASSIGNMENT_MODULUS, COMPOUND_ASSIGNMENT_NOT, GREATER_OR_EQUAL,
		LESS_OR_EQUAL, COMPOUND_ASSIGNMENT_SHIFT_RIGHT,
		COMPOUND_ASSIGNMENT_SHIFT_LEFT, MINUS, PLUS, BITWISE_AND, BITWISE_OR,
		LOGICAL_NOT, ASSIGNMENT, MULTIPLICATION, SHIFT_LEFT, LESS, SHIFT_RIGHT,
		GREATER, BITWISE_XOR, MODULUS, DIVISION, BRACE_OPEN, BRACE_CLOSE,
		SUBSCRIPT_OPEN, SUBSCRIPT_CLOSE, PARENTHESES_OPEN, PARENTHESES_CLOSE,
		COMMA, SEMICOLON, TILDE, QUESTION_MARK, COLON, DOT,
		ELLIPSIS,
		
		AUTO(true), BREAK(true), CASE(true), CHAR(true), CONST(true), CONTINUE(true),
		DEFAULT(true), DO(true), DOUBLE(true), ELSE(true), ENUM(true), EXTERN(true),
		FLOAT(true), FOR(true), GOTO(true), IF(true), INT(true), LONG(true), REGISTER(true),
		RETURN(true), SHORT(true), SIGNED(true), SIZEOF(true), STATIC(true), STRUCT(true),
		SWITCH(true), TYPEDEF(true), UNION(true), UNSIGNED(true), VOID(true),
		VOLATILE(true), WHILE(true);
		
		private static Set<String> keywords;
		
		TokenType() {
			
		}
		
		TokenType(boolean keyword) {
			if (keyword) addKeyword(name().toLowerCase());
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
	}
	
	@Override
	public String toString() {
		return Integer.toString(lin) + ":" + Integer.toString(col) + "\t" + type.name() + (value != null ? "\t" + value : "");
	}
}
