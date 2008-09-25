package br.usp.pcs.compiler;

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
		IDENTIFIER, OPERATOR, STRING_LITERAL, CHAR_LITERAL,
		
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
		
		AUTO, BREAK, CASE, CHAR, CONST, CONTINUE,
		DEFAULT, DO, DOUBLE, ELSE, ENUM, EXTERN,
		FLOAT, FOR, GOTO, IF, INT, LONG, REGISTER,
		RETURN, SHORT, SIGNED, SIZEOF, STATIC, STRUCT,
		SWITCH, TYPEDEF, UNION, UNSIGNED, VOID,
		VOLATILE, WHILE
	}
	
	@Override
	public String toString() {
		return Integer.toString(lin) + ":" + Integer.toString(col) + "\t" + type.name() + "\t" + value;
	}
}