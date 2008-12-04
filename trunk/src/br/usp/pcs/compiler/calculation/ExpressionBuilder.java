package br.usp.pcs.compiler.calculation;

import java.util.ArrayDeque;
import java.util.Deque;

import br.usp.pcs.compiler.Token.TokenType;

public class ExpressionBuilder {

	private Deque<Expression> operands = new ArrayDeque<Expression>();
	private Deque<TokenType> operators = new ArrayDeque<TokenType>();
	
	public void firstElement(Expression operand) {
		if (!operands.isEmpty() || !operators.isEmpty())
			throw new AssertionError();
		operands.push(operand);
	}
	
	public void processElement(TokenType operator, Expression operand) {
		if (operands.isEmpty()) throw new AssertionError();
		int priority = getPrecedencePriority(operator);
		while (!operators.isEmpty() && priority <= getPrecedencePriority(operators.peek()))
			 executeOperator();
		operands.push(operand);
		operators.push(operator);
	}
	
	public Expression finalizeExpression() {
		while (!operators.isEmpty()) executeOperator();
		Expression e = operands.pop();
		if (e == null) e = ExpressionUtils.voidExpression();
		return e;
	}

	private void executeOperator() {
		Expression e2 = operands.pop();
		Expression e1 = operands.pop();
		TokenType op = operators.pop();
		
		ExpressionUtils.checkCompatibleMathOperation(e1, e2);
		
		Expression e;
		
		switch (op) {
		case PLUS:
			e = ExpressionUtils.add(e1, e2);
			break;
		case MINUS:
			e = ExpressionUtils.subtract(e1, e2);
			break;
		case MULTIPLICATION:
			e = ExpressionUtils.multiply(e1, e2);
			break;
		case DIVISION:
			e = ExpressionUtils.divide(e1, e2);
			break;
		case MODULO:
			e = ExpressionUtils.modulo(e1, e2);
			break;
		case EQUAL:
			e = ExpressionUtils.equal(e1, e2);
			break;
		case NOT_EQUAL:
			e = ExpressionUtils.notEqual(e1, e2);
			break;
		case LESS:
			e = ExpressionUtils.lessThan(e1, e2);
			break;
		case LESS_OR_EQUAL:
			e = ExpressionUtils.lessThanOrEqualTo(e1, e2);
			break;
		case GREATER:
			e = ExpressionUtils.greaterThan(e1, e2);
			break;
		case GREATER_OR_EQUAL:
			e = ExpressionUtils.greaterThanOrEqualTo(e1, e2);
			break;
		case LOGICAL_AND:
			e = ExpressionUtils.logicalAnd(e1, e2);
			break;
		case LOGICAL_OR:
			e = ExpressionUtils.logicalOr(e1, e2);
			break;
		default:
			throw new RuntimeException("unexpected binary operator: " + op.toString());
		}
		
		operands.push(e);
	}
	
	private int getPrecedencePriority(TokenType op) {
		switch (op) {
		case LOGICAL_OR:
			return 0;
		case LOGICAL_AND:
			return 1;
		case EQUAL:
		case NOT_EQUAL:
			return 2;
		case LESS:
		case LESS_OR_EQUAL:
		case GREATER:
		case GREATER_OR_EQUAL:
			return 3;
		case PLUS:
		case MINUS:
			return 4;
		case MULTIPLICATION:
		case DIVISION:
		case MODULO:
			return 5;
		default:
			throw new RuntimeException("unexpected binary operator: " + op.toString());
		}
	}

}
