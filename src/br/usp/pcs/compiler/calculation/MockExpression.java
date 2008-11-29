package br.usp.pcs.compiler.calculation;

import br.usp.pcs.compiler.calculation.CalculationUtils.Constant;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Type;

public class MockExpression extends Constant implements Expression {

	public MockExpression() {
		super(666);
	}

	public Type getType() {
		return PrimitiveType.intTypeInstance();
	}

}
