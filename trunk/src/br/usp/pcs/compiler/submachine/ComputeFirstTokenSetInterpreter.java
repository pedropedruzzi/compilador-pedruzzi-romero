package br.usp.pcs.compiler.submachine;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mvn.montador.TwoPassAssembler;
import br.usp.pcs.compiler.Lex;
import br.usp.pcs.compiler.LexicalParser;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;
import br.usp.pcs.compiler.calculation.Expression;
import br.usp.pcs.compiler.entity.CustomType;
import br.usp.pcs.compiler.entity.Function;
import br.usp.pcs.compiler.entity.Variable;
import br.usp.pcs.compiler.entity.type.Array;
import br.usp.pcs.compiler.entity.type.Field;
import br.usp.pcs.compiler.entity.type.PrimitiveType;
import br.usp.pcs.compiler.entity.type.Record;
import br.usp.pcs.compiler.entity.type.SizedArray;
import br.usp.pcs.compiler.entity.type.Type;
import br.usp.pcs.compiler.memory.CodeBuffer;
import br.usp.pcs.compiler.memory.Instruction;
import br.usp.pcs.compiler.memory.InternalVariableManager;
import br.usp.pcs.compiler.memory.MemoryMap;
import br.usp.pcs.compiler.memory.Instruction.Opcode;
import br.usp.pcs.compiler.scope.GlobalScope;
import br.usp.pcs.compiler.scope.LocalScope;
import br.usp.pcs.compiler.scope.Scope;


public class ComputeFirstTokenSetInterpreter implements SubMachineInterpreter {
	
	private static final String VAZIO = "__vazio";
	
	// set of interested submachines
	private Set<String> interested = new HashSet<String>();
	
	private Map<String, Set<String>> firstToken = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> firstSM = new HashMap<String, Set<String>>();
	
	private Map<String, Set<String>> finalFirst;
	
	private String currentMachine;
	
	public void machine(String id) {
		currentMachine = id;
		firstToken.put(id, new HashSet<String>());
		firstSM.put(id, new HashSet<String>());
	}
	
	public void finalState(int num) {
		finalState(num, null);
	}
	
	public void finalState(int num, SubMachineReturnAction smra) {
		if (num == 0) firstToken.get(currentMachine).add(VAZIO);
	}
	
	public void transition(int state, String tokenType, int next, SemanticAction ... sa) {
		if (state == 0) {
			firstToken.get(currentMachine).add(tokenType);
		}
	}
	
	public void subMachineCall(int state, String subMachineId, int next, SemanticAction ... sa) {
		interested.add(subMachineId);
		if (state == 0) {
			firstSM.get(currentMachine).add(subMachineId);
		}
	}

	public void eof() {
		finalFirst = new HashMap<String, Set<String>>();
		
		for (String subMachineId : interested) {
			close(subMachineId);
		}
	}

	private void close(String subMachineId) {
		if (!finalFirst.containsKey(subMachineId)) {
			Set<String> first = firstToken.get(subMachineId);
			for (String son : firstSM.get(subMachineId)) {
				close(son);
				first.addAll(firstToken.get(son));
			}
			if (first.contains(VAZIO)) throw new RuntimeException("submachine may produce an empty string: " + subMachineId);
			finalFirst.put(subMachineId, first);
		}
	}
	
	public Map<String, Set<String>> getFirstMap() {
		return finalFirst;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Teste t = new Teste();
		ComputeFirstTokenSetInterpreter fti = new ComputeFirstTokenSetInterpreter();
		inputData(fti, t);
		System.out.println(fti.getFirstMap());
		//System.exit(0);
		SubMachineCreator smc = new SubMachineCreator(fti.getFirstMap());
		inputData(smc, t);
		SubMachine S = smc.getMainSubMachine();
		
		System.out.println(S.transitions);
		//System.exit(0);
		
		Lex lex = new LexicalParser("res/teste2.c");
		//while (lex.hasToken()) System.out.println(lex.nextToken());
		
		System.out.println("resultados = " + S.execute(lex));
		
		// compilation ok if there is no more tokens left
		System.out.println(lex.hasToken());
		
		// check for programming errors! true expected
		System.out.println(t.cc.parent == null);
		
		t.mm.generateCode(t.cb);
		t.cb.write(System.out);
		
		PrintStream ps = new PrintStream("out/teste2.asm");
		t.cb.write(ps);
		ps.close();
		
		TwoPassAssembler tpa = new TwoPassAssembler("out/teste2.asm", "out/teste2.obj", "out/teste2.lst");
		tpa.assemble();
	}
	
	public static void inputData(SubMachineInterpreter i, Teste t) {
		i.machine("programa");
		i.finalState(0);
		i.transition(0, "int", 1, t.start, t.setType);
		i.transition(0, "char", 1, t.start, t.setType);
		
		i.transition(1, "id", 2, t.setId);
		i.subMachineCall(2, "r_funcao", 0);
		i.subMachineCall(2, "r_declaracao_variavel", 0);
		
		i.transition(1, "[", 8); // variavel
		i.transition(8, "constante", 10, t.arraySize);
		i.transition(8, "]", 11, t.arraySize);
		i.transition(10, "]", 11);
		i.transition(11, "[", 9);
		i.transition(9, "constante", 10, t.arraySize);
		i.transition(11, "id", 12, t.setArrayType, t.setId);
		i.subMachineCall(12, "r_declaracao_variavel", 0);
		
		i.transition(0, "void", 3, t.start); //funcao
		i.transition(3, "id", 4, t.setId);
		i.subMachineCall(4, "r_funcao", 0);
		
		i.transition(0, "type", 5, t.start);
		i.transition(5, "id", 6, t.setTypeId);
		
		i.subMachineCall(6, "r_declaracao_tipo", 0, t.registerType); // definicao de tipo

		i.transition(6, "[", 8, t.checkType); // variavel
		
		i.transition(6, "id", 7, t.checkType, t.setId); // variavel
		i.subMachineCall(7, "r_declaracao_variavel", 0);
		
		// r_declaracao_variavel = [ "=" expressao ] { "," id [ "=" expressao ] } ";" .
		i.machine("r_declaracao_variavel");
		i.transition(0, ";", 1, t.registerVariable);
		i.transition(0, "=", 2);
		i.transition(0, ",", 4, t.registerVariable);
		i.subMachineCall(2, "expressao", 3, t.varInitializer);
		i.transition(3, ";", 1, t.registerVariable);
		i.transition(3, ",", 4, t.registerVariable);
		i.transition(4, "id", 0, t.setId);
		i.finalState(1);
		
		// declaracao_variavel = tipo id r_declaracao_variavel .
		i.machine("declaracao_variavel");
		i.subMachineCall(0, "tipo", 2);
		i.transition(2, "id", 3, t.setId);
		i.subMachineCall(3, "r_declaracao_variavel", 1);
		i.finalState(1);
		
		// tipo = ( "int" | "char" | "type" id ) [ "[" "]" ] { "[" constante "]" } .
		i.machine("tipo");
		i.transition(0, "int", 3, t.setType);
		i.transition(0, "char", 3, t.setType);
		i.transition(0, "type", 2);
		i.transition(2, "id", 3, t.setTypeId, t.checkType);
		i.finalState(3);
		i.transition(3, "[", 4);
		i.transition(4, "]", 5, t.arraySize);
		i.transition(4, "constante", 7, t.arraySize);
		i.finalState(5, t.setArrayTypeAndReturn);
		i.transition(5, "[", 6);
		i.transition(6, "constante", 7, t.arraySize);
		i.transition(7, "]", 5);
		
		// r_declaracao_tipo = "{" tipo id ";" { tipo id ";" } "}" ";" .
		i.machine("r_declaracao_tipo");
		i.transition(0, "{", 2, t.typeDefinition, t.checkDuplicatedType);
		i.subMachineCall(2, "tipo", 3);
		i.transition(3, "id", 4);
		i.transition(4, ";", 5);
		i.transition(5, "}", 6);
		i.subMachineCall(5, "tipo", 3);
		i.transition(6, ";", 1);
		i.finalState(1);
		
		// r_funcao = "(" [ tipo id { "," tipo id } ] ")" ( "{" { declaracao_variavel } { comando } "}" | ";" ) .
		// ok
		i.machine("r_funcao");
		i.transition(0, "(", 5, t.checkDuplicatedFunction, t.functionStart);
		i.transition(5, ")", 6);
		i.subMachineCall(5, "tipo", 7);
		i.transition(7, "id", 8, t.functionArgument);
		i.transition(8, ")", 6);
		i.transition(8, ",", 9);
		i.subMachineCall(9, "tipo", 7);
		i.transition(6, ";", 1);
		i.transition(6, "{", 10, t.registerFunction);
		i.subMachineCall(10, "declaracao_variavel", 10);
		i.transition(10, "}", 1, t.returnVoid, t.destroyScope);
		i.subMachineCall(10, "comando", 11);
		i.subMachineCall(11, "comando", 11);
		i.transition(11, "}", 1, t.returnVoid, t.destroyScope);
		i.finalState(1);
		
		// comando = "{" { declaracao_variavel } { comando } "}" | expressao ";" | "if" "(" expressao ")" comando [ "else" comando ] | "while" "(" expressao ")" comando | "for" "(" expressao ";" expressao ";" expressao ")" comando | "return" expressao ";" | "continue" ";" | "break" ";" | ";" .
		i.machine("comando");
		i.transition(0, "{", 2, t.openNewScope, t.newCommandContext);
		i.subMachineCall(0, "expressao", 4, t.evaluateE);
		i.transition(0, "continue", 4, t.continueLoop);
		i.transition(0, "break", 4, t.breakLoop);
		i.transition(0, "return", 5);
		i.transition(0, ";", 1);
		i.transition(0, "if", 6);
		i.transition(0, "for", 12);
		i.transition(0, "while", 20);
		i.subMachineCall(2, "declaracao_variavel", 2);
		i.transition(2, "}", 1, t.destroyScope);
		i.subMachineCall(2, "comando", 3);
		i.subMachineCall(3, "comando", 3);
		i.transition(3, "}", 1, t.destroyScope, t.destroyCommandContext);
		i.transition(4, ";", 1);
		i.subMachineCall(5, "expressao", 4, t.returnE);
		i.transition(6, "(", 7);
		i.subMachineCall(7, "expressao", 8, t.ifE);
		i.transition(8, ")", 9, t.newCommandContext);
		i.subMachineCall(9, "comando", 10, t.destroyCommandContext);
		i.finalState(10, t.noElse);
		i.transition(10, "else", 11, t.hasElse, t.newCommandContext);
		i.subMachineCall(11, "comando", 1, t.destroyCommandContext, t.endIf);
		i.transition(12, "(", 13);
		i.subMachineCall(13, "expressao", 14, t.forE1);
		i.transition(14, ";", 15);
		i.subMachineCall(15, "expressao", 16, t.forE2);
		i.transition(16, ";", 17);
		i.subMachineCall(17, "expressao", 18, t.forE3);
		i.transition(18, ")", 19, t.newCommandContext);
		i.subMachineCall(19, "comando", 1, t.destroyCommandContext, t.endFor);
		i.transition(20, "(", 21);
		i.subMachineCall(21, "expressao", 22, t.whileE);
		i.transition(22, ")", 23, t.newCommandContext);
		i.subMachineCall(23, "comando", 1, t.destroyCommandContext, t.endWhile);
		i.finalState(1);
		

		i.machine("expressao");
		// String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&", "&&", "|", "||", "^", ">>", "<<" };
		// String[] op1 = { "!", "~", "-" };
		String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&&", "||" };
		String[] op1 = { "!", "-" };
		
		i.transition(0, "constante", 3);
		i.transition(0, "string", 3);
		
		i.transition(0, "(", 1);
		i.subMachineCall(1, "expressao", 2);
		i.transition(2, ")", 3);
		
		i.finalState(3, t.stubE);
		
		for (String op : op1) {
			i.transition(0, op, 4);
			i.transition(4, op, 4);
		}
		
		// 4 ===e3==> 3

		i.transition(4, "constante", 3);
		i.transition(4, "string", 3);
		
		i.transition(4, "(", 5);
		i.subMachineCall(5, "expressao", 6);
		i.transition(6, ")", 3);
		
		i.transition(4, "id", 7);
		i.finalState(7, t.stubE);
		for (String op : op2)
			i.transition(7, op, 8);
		
		i.subMachineCall(7, "r_lvalue", 3);
		i.transition(7, "(", 9);
		i.transition(9, ")", 3);
		i.subMachineCall(9, "expressao", 10);
		i.transition(10, ")", 3);
		i.transition(10, ",", 11);
		i.subMachineCall(11, "expressao", 10);
		
		// FIM
		
		for (String op : op2)
			i.transition(3, op, 8);
		
		for (String op : op1)
			i.transition(8, op, 8);
		
		// 8 ===e3==> 3

		i.transition(8, "constante", 3);
		i.transition(8, "string", 3);
		i.transition(8, "(", 5);
		i.transition(8, "id", 7);
		
		// FIM
		
		i.transition(0, "id", 12);
		i.finalState(12, t.stubE);
		i.transition(12, "=", 0);
		i.subMachineCall(12, "r_lvalue", 13);
		i.finalState(13, t.stubE);
		i.transition(13, "=", 0);
		
		for (String op : op2) {
			i.transition(12, op, 8);
			i.transition(13, op, 8);
		}
		
		i.transition(12, "(", 14);
		i.transition(14, ")", 3);
		i.subMachineCall(14, "expressao", 16);
		i.transition(16, ")", 3);
		i.transition(16, ",", 17);
		i.subMachineCall(17, "expressao", 16);
		
		
		i.machine("r_lvalue");
		i.finalState(1, t.stubE);
		i.transition(0, ".", 2);
		i.transition(1, ".", 2);
		i.transition(2, "id", 1);
		i.transition(0, "[", 3);
		i.transition(1, "[", 3);
		i.subMachineCall(3, "expressao", 4);
		i.transition(4, "]", 1);
		
		
		
		i.eof();
		
		
	}
	
	private static class Teste {
		
		protected static final int UNKOWN_SIZE = -1;
		private CodeBuffer cb = new CodeBuffer();
		private Scope scope = new GlobalScope();
		private Scope newScope;
		private MemoryMap mm = new MemoryMap();
		private InternalVariableManager vm = new InternalVariableManager(mm);
		private Type type;
		private Variable result;
		private String returnAddress;
		private List<Variable> arguments;
		
		private String id;
		private String typeId;
		private String functionId;
		
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
				if (scope.containsFunction(id)) error("duplicated function definition: " + id);
			}
		};
		
		public SemanticAction checkDuplicatedType = new SemanticAction() {
			public void doAction(Object o) {
				if (scope.containsCustomType(id)) error("duplicated type definition: " + id);
			}
		};
		
		public SemanticAction typeDefinition = new SemanticAction() {
			public void doAction(Object o) {
				id = typeId;
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
				scope.registerSymbol(id, new Variable(mm.allocVariable(id, (short) initializer.getValue()), type));

				// TODO tratar o caso de string!!
				// pode ser estático ou dinâmico. o segundo caso preciso gerar código
				/*
				if (initializer instanceof Integer) {
					scope.registerSymbol(id, new Variable(mm.allocVariable(id, ((Integer) initializer).shortValue()), type));
				} else if (initializer instanceof String) {
					scope.registerSymbol(id, new Variable(mm.allocPointer(id, (String) initializer), type));
				}
				*/
			}
		};
		
		public SemanticAction varInitializer = new SemanticAction() {
			public void doAction(Object o) {
				initializer = (Expression) o;
			}
		};
		
		public SemanticAction registerType = new SemanticAction() {
			public void doAction(Object result) {
				// TODO:
				scope.registerSymbol(id, new CustomType(new Record(new Field[0])));
			}
		};
		
		public SemanticAction functionStart = new SemanticAction() {
			public void doAction(Object o) {
				functionId = id;
				if (type != null)
					result = new Variable(mm.allocVariable("ret_" + functionId), type);
				else
					result = null;
				arguments = new LinkedList<Variable>();
				// new scope
				newScope = new LocalScope(scope);
			}
		};
		
		public SemanticAction functionArgument = new SemanticActionWithToken() {
			public void doAction(Token t) {
				String id = (String) t.getValue();
				Variable var = new Variable(mm.allocVariable(functionId + "_" + id), type);
				arguments.add(var);
				newScope.registerVariable(id, var);
			}
		};
		
		public SemanticAction registerFunction = new SemanticAction() {
			public void doAction(Object o) {
				returnAddress = mm.label(functionId);
				scope.registerSymbol(functionId, new Function(returnAddress, result, arguments.toArray(new Variable[arguments.size()])));
				cb.addInstruction(new Instruction(returnAddress, Opcode.CONSTANT, 0));
				scope = newScope;
				newScope = null;
				arguments = null;
				functionId = null;
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
		
		public SemanticAction checkAccess = new SemanticActionWithToken() {
			public void doAction(Token t) {
				switch (t.getType()) {
				case DOT:
					if (!(type instanceof Record)) error(t, "not a record");
					break;
				case SUBSCRIPT_OPEN:
					if (!(type instanceof Array)) error(t, "not an array");
					break;
				}
			}
		};
		

		private CommandContext cc = new CommandContext(null);
		private static class CommandContext {
			CommandContext parent;
			String l1;
			String l2;
			String i1;
			String i2;
			protected Expression e;
			public CommandContext(CommandContext parent) {
				this.parent = parent;
			}
		}
		
		public SemanticAction newCommandContext = new SemanticAction() {
			public void doAction(Object result) {
				cc = new CommandContext(cc);
			}
		};
		
		public SemanticAction destroyCommandContext = new SemanticAction() {
			public void doAction(Object result) {
				cc = cc.parent;
			}
		};
		
		public SemanticAction continueLoop = new SemanticAction() {
			public void doAction(Object result) {
				if (cc.l1 == null) error("not in a loop");
				cb.addInstruction(new Instruction(Opcode.JP, cc.l1));
			}
		};
		
		public SemanticAction breakLoop = new SemanticAction() {
			public void doAction(Object result) {
				if (cc.l2 == null) error("not in a loop");
				cb.addInstruction(new Instruction(Opcode.JP, cc.l2));
			}
		};
		
		public SemanticAction evaluateE = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				e.evaluate(cb, vm);
			}
		};
		
		public SemanticAction returnE = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				e.evaluate(cb, vm);
				if (result == null) {
					warn("`return' with a value, in function returning void");
				} else {
					cb.addInstruction(new Instruction(Opcode.STORE, result.getAddress()));
				}
				cb.addInstruction(new Instruction(Opcode.RETURN, returnAddress));
			}
		};
		
		public SemanticAction returnVoid = new SemanticAction() {
			public void doAction(Object r) {
				cb.addInstruction(new Instruction(Opcode.RETURN, returnAddress));
			}
		};
		
		public SemanticAction ifE = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				e.evaluate(cb, vm);
				cc.i1 = mm.label("if1");
				cb.addInstruction(new Instruction(Opcode.JZ, cc.i1));
			}
		};
		
		public SubMachineReturnAction noElse = new SubMachineReturnAction() {
			public Object returnAction() {
				cb.setNextLabel(cc.i1);
				cc.i1 = null;
				return true;
			}
		};
		
		public SemanticAction hasElse = new SemanticAction() {
			public void doAction(Object r) {
				cc.i2 = mm.label("if2");
				cb.addInstruction(new Instruction(Opcode.JP, cc.i2));
				cb.setNextLabel(cc.i1);
			}
		};
		
		public SemanticAction endIf = new SemanticAction() {
			public void doAction(Object r) {
				cb.setNextLabel(cc.i2);
				cc.i1 = null;
				cc.i2 = null;
			}
		};
		
		public SemanticAction forE1 = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				e.evaluate(cb, vm);
			}
		};
		
		public SemanticAction forE2 = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				cc.l1 = cb.getNextLabel();
				if (cc.l1 == null) {
					cc.l1 = mm.label("for1");
					cb.setNextLabel(cc.l1);
				}
				e.evaluate(cb, vm);
				cc.l2 = mm.label("for2");
				cb.addInstruction(new Instruction(Opcode.JZ, cc.l2));
			}
		};
		
		public SemanticAction forE3 = new SemanticAction() {
			public void doAction(Object r) {
				cc.e = (Expression) r;
			}
		};
		
		public SemanticAction endFor = new SemanticAction() {
			public void doAction(Object r) {
				cc.e.evaluate(cb, vm);
				cb.addInstruction(new Instruction(Opcode.JP, cc.l1));
				cb.setNextLabel(cc.l2);
				cc.e = null;
				cc.l1 = null;
				cc.l2 = null;
			}
		};
		
		public SemanticAction whileE = new SemanticAction() {
			public void doAction(Object r) {
				Expression e = (Expression) r;
				cc.l1 = cb.getNextLabel();
				if (cc.l1 == null) {
					cc.l1 = mm.label("while1");
					cb.setNextLabel(cc.l1);
				}
				e.evaluate(cb, vm);
				cc.l2 = mm.label("while2");
				cb.addInstruction(new Instruction(Opcode.JZ, cc.l2));
			}
		};
		
		public SemanticAction endWhile = new SemanticAction() {
			public void doAction(Object r) {
				cb.addInstruction(new Instruction(Opcode.JP, cc.l1));
				cb.setNextLabel(cc.l2);
				cc.l1 = null;
				cc.l2 = null;
			}
		};
		

		
		public SubMachineReturnAction stubE = new SubMachineReturnAction() {
			public Object returnAction() {
				return new Expression();
			}
		};
		

		private void error(Token token, String text) {
			error("error on token: " + token.toString() + ": " + text);
		}
		private void warn(String text) {
			System.out.println("Warning: " + text);
		}

		private void error(String text) {
			throw new RuntimeException(text);
		}
		
		
	}
	
}
