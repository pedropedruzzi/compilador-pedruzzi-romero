package br.usp.pcs.compiler.semantic;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;
import br.usp.pcs.compiler.calculation.CalculationUtils;
import br.usp.pcs.compiler.calculation.Expression;
import br.usp.pcs.compiler.calculation.ExpressionBuilder;
import br.usp.pcs.compiler.calculation.ExpressionUtils;
import br.usp.pcs.compiler.calculation.FunctionCall;
import br.usp.pcs.compiler.calculation.LValue;
import br.usp.pcs.compiler.calculation.ExpressionUtils.StringConstant;
import br.usp.pcs.compiler.entity.CustomType;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.Field;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Record;
import br.usp.pcs.compiler.entity.type.SizedArray;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.Instruction.Opcode;
import br.usp.pcs.compiler.scope.GlobalScope;
import br.usp.pcs.compiler.scope.LocalScope;
import br.usp.pcs.compiler.scope.Scope;
import br.usp.pcs.compiler.submachine.SemanticAction;
import br.usp.pcs.compiler.submachine.SemanticActionWithToken;
import br.usp.pcs.compiler.submachine.SubMachineReturnAction;

public class Semantic {
	
	private CompilationUnit cu;
	private Scope scope = new GlobalScope();
	
	public Semantic(CompilationUnit cu) {
		this.cu = cu;
	}
	
	
	private CommandContext command;
	private class CommandContext {
		private final CommandContext parent;
		private String l1;
		private String l2;
		private String i1;
		private String i2;
		private Expression e;
		
		public CommandContext(CommandContext parent) {
			this.parent = parent;
		}
	}
	
	public SemanticAction newCommandContext = new SemanticAction() {
		public void doAction(Object result) {
			command = new CommandContext(command);
		}
	};
	
	public SemanticAction destroyCommandContext = new SemanticAction() {
		public void doAction(Object result) {
			command = command.parent;
		}
	};
		
	public SemanticAction continueLoop = new SemanticAction() {
		public void doAction(Object result) {
			if (command.l1 == null) error("not in a loop");
			cu.cb.addInstruction(new Instruction(Opcode.JP, command.l1));
		}
	};
	
	public SemanticAction breakLoop = new SemanticAction() {
		public void doAction(Object result) {
			if (command.l2 == null) error("not in a loop");
			cu.cb.addInstruction(new Instruction(Opcode.JP, command.l2));
		}
	};
	
	public SemanticAction evaluateE = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			e.evaluate(cu);
		}
	};
	
	public SemanticAction returnE = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			if (returnType == null) {
				warn("`return' with a value, in function returning void");
			} else if (!ExpressionUtils.isCompatible(returnType, e.getType())) {
				error("return type not compatible");
			}
			e.evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.RETURN, returnAddress));
		}
	};
	
	public SemanticAction forceReturn = new SemanticAction() {
		public void doAction(Object r) {
			cu.cb.addInstruction(new Instruction(Opcode.RETURN, returnAddress));
		}
	};
	
	public SemanticAction returnVoid = new SemanticAction() {
		public void doAction(Object r) {
			if (returnType != null)
				warn("`return' with no value, in function returning non-void");
			cu.cb.addInstruction(new Instruction(Opcode.RETURN, returnAddress));
		}
	};
	
	public SemanticAction ifE = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			command.i1 = cu.mm.label("if1");
			e.branchIfZero(cu, command.i1);
		}
	};
	
	public SubMachineReturnAction noElse = new SubMachineReturnAction() {
		public Object returnAction() {
			cu.cb.setNextLabel(command.i1);
			command.i1 = null;
			return true;
		}
	};
	
	public SemanticAction hasElse = new SemanticAction() {
		public void doAction(Object r) {
			command.i2 = cu.mm.label("if2");
			cu.cb.addInstruction(new Instruction(Opcode.JP, command.i2));
			cu.cb.setNextLabel(command.i1);
		}
	};
	
	public SemanticAction endIf = new SemanticAction() {
		public void doAction(Object r) {
			cu.cb.setNextLabel(command.i2);
			command.i1 = null;
			command.i2 = null;
		}
	};
	
	public SemanticAction forE1 = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			e.evaluate(cu);
		}
	};
	
	public SemanticAction forE2 = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			command.l1 = cu.cb.getNextLabel();
			if (command.l1 == null) {
				command.l1 = cu.mm.label("for1");
				cu.cb.setNextLabel(command.l1);
			}
			command.l2 = cu.mm.label("for2");
			e.branchIfZero(cu, command.l2);
		}
	};
	
	public SemanticAction forE3 = new SemanticAction() {
		public void doAction(Object r) {
			command.e = (Expression) r;
		}
	};
	
	public SemanticAction endFor = new SemanticAction() {
		public void doAction(Object r) {
			command.e.evaluate(cu);
			cu.cb.addInstruction(new Instruction(Opcode.JP, command.l1));
			cu.cb.setNextLabel(command.l2);
			command.e = null;
			command.l1 = null;
			command.l2 = null;
		}
	};
	
	public SemanticAction whileE = new SemanticAction() {
		public void doAction(Object r) {
			Expression e = (Expression) r;
			command.l1 = cu.cb.getNextLabel();
			if (command.l1 == null) {
				command.l1 = cu.mm.label("while1");
				cu.cb.setNextLabel(command.l1);
			}
			command.l2 = cu.mm.label("while2");
			e.branchIfZero(cu, command.l2);
		}
	};
	
	public SemanticAction endWhile = new SemanticAction() {
		public void doAction(Object r) {
			cu.cb.addInstruction(new Instruction(Opcode.JP, command.l1));
			cu.cb.setNextLabel(command.l2);
			command.l1 = null;
			command.l2 = null;
		}
	};
	
	
	private ExpressionContext expression = new ExpressionContext(null);
	private class ExpressionContext {
		private Token idToken;
		private Expression current;
		//private Expression previous;
		private ExpressionBuilder eb = new ExpressionBuilder();
		private LValue lValue;
		private FunctionCall fCall;
		
		private Set<LValue> lValues = new HashSet<LValue>();
		private Queue<TokenType> op1 = new ArrayDeque<TokenType>();
		
		
		private final ExpressionContext parent;
		protected TokenType op2;
		public ExpressionContext(ExpressionContext parent) {
			this.parent = parent;
		}
	}
	
	public SemanticAction newExpressionContext = new SemanticAction() {
		public void doAction(Object o) {
			expression = new ExpressionContext(expression);
		}
	};
	
	public SemanticAction destroyExpressionContext = new SemanticAction() {
		public void doAction(Object o) {
			expression = expression.parent;
		}
	};
	
	public SemanticAction setIdE = new SemanticActionWithToken() {
		public void doAction(Token t) {
			expression.idToken = t;
		}
	};
	
	
	/** function call **/
	public SemanticAction checkFunction = new SemanticAction() {
		public void doAction(Object o) {
			String id = (String) expression.idToken.getValue();
			if (!scope.containsFunction(id)) error(expression.idToken, "not a function");
			expression.fCall = new FunctionCall(scope.retrieveFunction(id));
		}
	};
	
	public SemanticAction functionArgument = new SemanticAction() {
		public void doAction(Object o) {
			Expression arg = (Expression) o;
			expression.fCall.processArgument(arg);
		}
	};
	
	public SemanticAction functionCallEnd = new SemanticAction() {
		public void doAction(Object o) {
			if (!expression.fCall.isComplete())
				error("missing arguments in function call");
			expression.current = expression.fCall;
		}
	};

	
	/** lvalue **/
	public SemanticAction checkVar = new SemanticAction() {
		public void doAction(Object o) {
			String id = (String) expression.idToken.getValue();
			if (!scope.containsVariable(id)) error(expression.idToken, "not a variable");
			expression.lValue = new LValue(scope.retrieveVariable(id));
		}
	};
	
	// checkVar + lValueEnd + endE
	public SubMachineReturnAction checkVarEndE = new SubMachineReturnAction() {
		public Object returnAction() {
			checkVar.doAction(null);
			lValueEnd.doAction(null);
			return endE.returnAction();
		}
	};
	
	public SemanticAction accessRecordMember = new SemanticActionWithToken() {
		public void doAction(Token t) {
			String member = (String) t.getValue();
			expression.lValue.accessRecordMember(member);
		}
	};
	
	public SemanticAction accessArrayElement = new SemanticAction() {
		public void doAction(Object o) {
			Expression index = (Expression) o;
			expression.lValue.accessArrayElement(index);
		}
	};
	
	public SemanticAction lValueEnd = new SemanticAction() {
		public void doAction(Object o) {
			expression.current = expression.lValue;
		}
	};
	
	/** constant **/
	public SemanticAction constant = new SemanticActionWithToken() {
		public void doAction(Token t) {
			expression.current = ExpressionUtils.integerConstant((Integer) t.getValue());
		}
	};
	
	/** string **/
	public SemanticAction string = new SemanticActionWithToken() {
		public void doAction(Token t) {
			String str = (String) t.getValue();
			byte[] content = str.getBytes(Charset.forName("ascii"));
			content = Arrays.copyOf(content, (content.length + 2) & ~1);
			String address = cu.mm.allocArea("string", content);
			expression.current = ExpressionUtils.stringConstant(address);
		}
	};
	
	
	public SemanticAction assignment = new SemanticAction() {
		public void doAction(Object o) {
			if (expression.lValue.getType() != PrimitiveType.intTypeInstance())
				error("can only assign integer lvalues");
			
			expression.lValues.add(expression.lValue);
		}
	};
	
	
	public SemanticAction op1 = new SemanticActionWithToken() {
		public void doAction(Token t) {
			expression.op1.add(t.getType());
		}
	};
	
	private void processExpressionElement(Queue<TokenType> op1, TokenType op2, Expression e) {
		if (!op1.isEmpty())
			e = ExpressionUtils.applyUnaryOperators(op1, e);
		if (expression.op2 == null)
			expression.eb.firstElement(e);
		else
			expression.eb.processElement(op2, e);
	}
	
	public SemanticAction op2 = new SemanticActionWithToken() {
		public void doAction(Token t) {
			processExpressionElement(expression.op1, expression.op2, expression.current);
			
			expression.current = null;
			expression.op2 = t.getType();
		}
	};
	
	public SubMachineReturnAction endE = new SubMachineReturnAction() {
		public Object returnAction() {
			processExpressionElement(expression.op1, expression.op2, expression.current);
			expression.current = null;
			expression.op1.clear();
			expression.op2 = null;
			
			Expression e = expression.eb.finalizeExpression();
			
			if (!expression.lValues.isEmpty())
				e = ExpressionUtils.assignment(expression.lValues, e);
			
			expression.lValues = new HashSet<LValue>();
			
			return e;
		}
	};
	
	public SemanticAction subExpression = new SemanticAction() {
		public void doAction(Object o) {
			expression.current = (Expression) o;
		}
	};
	
	
	
	private String functionId;
	private Type returnType;
	private List<Variable> arguments;
	private Scope newScope;
	private String returnAddress;
	
	public SemanticAction functionStart = new SemanticAction() {
		public void doAction(Object o) {
			functionId = id;
			returnType = type;
			arguments = new LinkedList<Variable>();
			newScope = new LocalScope(scope);
		}
	};
	
	public SemanticAction functionArgumentDeclaration = new SemanticActionWithToken() {
		public void doAction(Token t) {
			String id = (String) t.getValue();
			Variable var = new Variable(cu.mm.allocVariable(functionId + "_" + id), type);
			arguments.add(var);
			newScope.registerVariable(id, var);
		}
	};
	
	public SemanticAction registerFunction = new SemanticAction() {
		public void doAction(Object o) {
			returnAddress = cu.mm.globalLabel(functionId);
			scope.registerSymbol(functionId, new Function(returnAddress, returnType, arguments.toArray(new Variable[arguments.size()])));
			cu.cb.addInstruction(new Instruction(returnAddress, Opcode.CONSTANT, 0));
			scope = newScope;
			newScope = null;
			arguments = null;
			functionId = null;
		}
	};
	
	
	
	private static final int UNKOWN_SIZE = -1;
	private Type type;
	private String id;
	private String typeId;
	
	private List<Integer> arraySizes = new ArrayList<Integer>();
	
	private Expression initializer;

	public SemanticAction start = new SemanticActionWithToken() {
		public void doAction(Token token) {
			id = typeId = null;
			type = null;
			arraySizes.clear();
			// inicializa tudo
		}
	};
	
	public SemanticAction setId = new SemanticActionWithToken() {
		public void doAction(Token token) {
			id = (String) token.getValue();
		}
	};
	
	public SemanticAction checkDuplicatedFunction = new SemanticAction() {
		public void doAction(Object o) {
			if (scope.containsFunction(id)) {
				Function f = scope.retrieveFunction(id);
				if (f.isDefined()) error("duplicated function definition: " + id);
			}
		}
	};
	
	public SemanticAction setTypeId = new SemanticActionWithToken() {
		public void doAction(Token token) {
			typeId = (String) token.getValue();
		}
	};
	
	public SemanticAction checkType = new SemanticAction() {
		public void doAction(Object o) {
			if (!scope.containsCustomType(typeId)) error("type `" + typeId + "' is not defined");
			type = scope.retrieveCustomType(typeId).getDefinition();
		}
	};
	
	public SemanticAction setType = new SemanticActionWithToken() {
		public void doAction(Token t) {
			switch (t.getType()) {
			case CHAR:
				type = PrimitiveType.charTypeInstance();
				break;
			case INT:
				type = PrimitiveType.intTypeInstance();
				break;
			}
		}
	};
	
	public SemanticAction setArrayType = new SemanticAction() {
		public void doAction(Object o) {
			Collections.reverse(arraySizes);
			for (int i : arraySizes) {
				if (i == UNKOWN_SIZE) type = new Array(type);
				else type = new SizedArray(type, i);
			}
		}
	};
	
	public SubMachineReturnAction setArrayTypeAndReturn = new SubMachineReturnAction() {
		public Object returnAction() {
			setArrayType.doAction(null);
			return type; // precisa?
		}
	};
	
	public SemanticAction registerVariable = new SemanticAction() {
		public void doAction(Object result) {
			if (scope.containsVariableCurrentScope(id)) error("duplicated variable: " + id);
			Variable var = null;
			if (scope.isGlobal() && initializer != null) {
				if (CalculationUtils.isConstant(initializer)) {
					if (type != PrimitiveType.intTypeInstance())
						error("this initializer is only allowed for integer variables");
					var = new Variable(cu.mm.allocGlobalVariable(id, (short) CalculationUtils.getValue(initializer)), type);
				} else {
					if (initializer instanceof StringConstant) {
						if (!isCharArray(type))
							error("wrong var type with a string initializer. expected char[]");
						StringConstant sc = (StringConstant) initializer;
						var = new Variable(cu.mm.allocGlobalPointer(id, sc.getAddress()), type);
					} else {
						error("static initializer must be a constant expression. " + initializer.toString());
					}
				}
			} else {
				// local scope OR no initializer
				String address;
				if (type instanceof PrimitiveType) {
					if (scope.isGlobal()) {
						address = cu.mm.allocGlobalVariable(id, (short)0);
					} else {
						address = cu.mm.allocVariable(id);
						if (initializer != null) {
							if (type != PrimitiveType.intTypeInstance())
								error("this initializer is only allowed for integer variables");
							initializer.evaluate(cu);
							cu.cb.addInstruction(new Instruction(Opcode.STORE, address));
						}
					}
				} else {
					// non-primitive type
					int size = type.sizeOf();
					if (scope.isGlobal()) {
						address = cu.mm.allocPointedAreaGlobal(id, size);
					} else {
						address = cu.mm.allocPointedArea(id, size);
					}
				}
				var = new Variable(address, type);
				
			}
			
			scope.registerSymbol(id, var);
			initializer = null;
		}

		private boolean isCharArray(Type type) {
			if (type instanceof Array) {
				Array a = (Array) type;
				return a.getInnerType() == PrimitiveType.charTypeInstance();
			} else return false;
		}
	};
	
	public SemanticAction varInitializer = new SemanticAction() {
		public void doAction(Object o) {
			initializer = (Expression) o;
		}
	};
	
	

	private List<Field> fields;
	public SemanticAction typeDefinition = new SemanticAction() {
		public void doAction(Object o) {
			id = typeId;
			fields = new LinkedList<Field>();
		}
	};
	
	public SemanticAction checkDuplicatedType = new SemanticAction() {
		public void doAction(Object o) {
			if (scope.containsCustomType(id)) error("duplicated type definition: " + id);
		}
	};
	
	public SemanticAction field = new SemanticActionWithToken() {
		public void doAction(Token t) {
			String id = (String) t.getValue();
			fields.add(new Field(id, type));
		}
	};
	
	public SemanticAction registerType = new SemanticAction() {
		public void doAction(Object result) {
			scope.registerCustomType(id, new CustomType(new Record(fields.toArray(new Field[fields.size()]))));
		}
	};
	
	
	
	
	
	public SemanticAction arraySize = new SemanticActionWithToken() {
		public void doAction(Token token) {
			if (token.getType() == TokenType.INTEGER_LITERAL) {
				int size = (Integer) token.getValue();
				if (size <= 0) error(token, "array size must be positive");
				arraySizes.add(size);
			} else {
				arraySizes.add(UNKOWN_SIZE);
			}
		}
	};
	
	
	

	public SemanticAction openNewScope = new SemanticAction() {
		public void doAction(Object result) {
			scope = new LocalScope(scope);
		}
	};
	
	public SemanticAction destroyScope = new SemanticAction() {
		public void doAction(Object result) {
			scope = scope.getParent();
		}
	};
	

	private void error(Token token, String text) {
		error("error on token: " + token.toString() + ": " + text);
	}
	private void warn(String text) {
		System.out.println("warning: " + text);
	}

	private void error(String text) {
		throw new CompilationException(text);
	}
	
	public void checkEnd() {
		if (command != null) throw new RuntimeException("command context should be null!");
		if (expression.parent != null) throw new RuntimeException("expression parent context should be null!");
	}

}
