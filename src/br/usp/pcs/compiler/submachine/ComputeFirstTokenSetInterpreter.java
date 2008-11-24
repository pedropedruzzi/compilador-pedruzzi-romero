package br.usp.pcs.compiler.submachine;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Lex;
import br.usp.pcs.compiler.LexicalParser;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;
import br.usp.pcs.compiler.calculation.Calculation;
import br.usp.pcs.compiler.symbol.CustomType;
import br.usp.pcs.compiler.symbol.Symbol;
import br.usp.pcs.compiler.symbol.type.Array;
import br.usp.pcs.compiler.symbol.type.PrimitiveType;
import br.usp.pcs.compiler.symbol.type.Record;
import br.usp.pcs.compiler.symbol.type.SizedArray;
import br.usp.pcs.compiler.symbol.type.Type;


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
	}
	
	public static void inputData(SubMachineInterpreter i, Teste t) {
		int state;
		
		i.machine("programa");
		i.finalState(0);
		i.transition(0, "int", 1, t.arraySize);
		i.transition(0, "char", 1, t.start);
		
		i.transition(1, "id", 2, t.setId, t.checkUnusedSymbol);
		i.subMachineCall(2, "r_funcao", 0);
		i.subMachineCall(2, "r_declaracao_variavel", 0, t.setVarType);
		
		i.transition(1, "[", 8); // variavel
		i.transition(8, "constante", 10, t.arraySize);
		i.transition(8, "]", 11, t.arraySize);
		i.transition(10, "]", 11);
		i.transition(11, "[", 9);
		i.transition(9, "constante", 10, t.arraySize);
		i.transition(11, "id", 12, t.setId, t.checkUnusedSymbol);
		i.subMachineCall(12, "r_declaracao_variavel", 0, t.setVarType);
		
		i.transition(0, "void", 3, t.start);
		i.transition(3, "id", 4, t.setId);
		i.subMachineCall(4, "r_funcao", 0);
		
		i.transition(0, "type", 5, t.start);
		i.transition(5, "id", 6, t.setTypeId);
		
		i.subMachineCall(6, "r_declaracao_tipo", 0, t.typeDefinition, t.checkUnusedSymbol); // definicao de tipo

		i.transition(6, "[", 8, t.checkType); // variavel
		
		i.transition(6, "id", 7, t.checkType, t.setId); // variavel
		i.subMachineCall(7, "r_declaracao_variavel", 0, t.setVarType);
		
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
		i.transition(2, "id", 3);
		i.subMachineCall(3, "r_declaracao_variavel", 1);
		i.finalState(1);
		
		// tipo = ( "int" | "char" | "type" id ) [ "[" "]" ] { "[" constante "]" } .
		i.machine("tipo");
		state = 0;
		i.transition(state, "int", 3);
		i.transition(state, "char", 3);
		i.transition(state, "type", 2);
		state = 2;
		i.transition(state, "id", 3);
		state = 3;
		i.finalState(state);
		i.transition(state, "[", 4);
		state = 4;
		i.transition(state, "]", 5);
		i.transition(state, "constante", 7);
		state = 5;
		i.finalState(state);
		i.transition(state, "[", 6);
		state = 6;
		i.transition(state, "constante", 7);
		state = 7;
		i.transition(state, "]", 5);
		
		// r_declaracao_tipo = "{" tipo id ";" { tipo id ";" } "}" ";" .
		i.machine("r_declaracao_tipo");
		i.transition(0, "{", 2);
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
		i.transition(0, "(", 5);
		i.transition(5, ")", 6); // continua
		i.subMachineCall(5, "tipo", 7);
		i.transition(7, "id", 8);
		i.transition(8, ")", 6);
		i.transition(8, ",", 9);
		i.subMachineCall(9, "tipo", 7);
		i.transition(6, ";", 1);
		i.transition(6, "{", 10);
		i.subMachineCall(10, "declaracao_variavel", 10);
		i.transition(10, "}", 1);
		i.subMachineCall(10, "comando", 11);
		i.subMachineCall(11, "comando", 11);
		i.transition(11, "}", 1);
		i.finalState(1);
		
		// comando = "{" { declaracao_variavel } { comando } "}" | expressao ";" | "if" "(" expressao ")" comando [ "else" comando ] | "while" "(" expressao ")" comando | "for" "(" expressao ";" expressao ";" expressao ")" comando | "return" expressao ";" | "continue" ";" | "break" ";" | ";" .
		i.machine("comando");
		state = 0;
		i.transition(state, "{", 2);
		i.subMachineCall(state, "expressao", 4);
		i.transition(state, "continue", 4);
		i.transition(state, "break", 4);
		i.transition(state, "return", 5);
		i.transition(state, ";", 1);
		i.transition(state, "if", 6);
		i.transition(state, "for", 12);
		i.transition(state, "while", 20);
		state = 2;
		i.subMachineCall(state, "declaracao_variavel", 2);
		i.transition(state, "}", 1);
		i.subMachineCall(state, "comando", 3);
		state = 3;
		i.subMachineCall(state, "comando", 3);
		i.transition(state, "}", 1);
		state = 4;
		i.transition(state, ";", 1);
		state = 5;
		i.subMachineCall(state, "expressao", 4);
		state = 6;
		i.transition(state, "(", 7);
		state = 7;
		i.subMachineCall(state, "expressao", 8);
		state = 8;
		i.transition(state, ")", 9);
		state = 9;
		i.subMachineCall(state, "comando", 10);
		state = 10;
		i.finalState(state);
		i.transition(state, "else", 11);
		state = 11;
		i.subMachineCall(state, "comando", 1);
		state = 12;
		i.transition(state, "(", 13);
		state = 13;
		i.subMachineCall(state, "expressao", 14);
		state = 14;
		i.transition(state, ";", 15);
		state = 15;
		i.subMachineCall(state, "expressao", 16);
		state = 16;
		i.transition(state, ";", 17);
		state = 17;
		i.subMachineCall(state, "expressao", 18);
		state = 18;
		i.transition(state, ")", 19);
		state = 19;
		i.subMachineCall(state, "comando", 1);
		state = 20;
		i.transition(state, "(", 21);
		state = 21;
		i.subMachineCall(state, "expressao", 22);
		state = 22;
		i.transition(state, ")", 23);
		state = 23;
		i.subMachineCall(state, "comando", 1);
		i.finalState(1);
		

		i.machine("expressao");
		String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&", "&&", "|", "||", "^", ">>", "<<" };
		String[] op1 = { "!", "~", "-" };
		
		i.transition(0, "constante", 3);
		i.transition(0, "string", 3);
		
		i.transition(0, "(", 1);
		i.subMachineCall(1, "expressao", 2);
		i.transition(2, ")", 3);
		
		i.finalState(3);
		
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
		i.finalState(7);
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
		i.finalState(12);
		i.transition(12, "=", 0);
		i.subMachineCall(12, "r_lvalue", 13);
		i.finalState(13);
		i.transition(13, "=", 0);
		
		for (String op : op2) {
			i.transition(12, op, 8);
			i.transition(13, op, 8);
		}
		
		i.transition(12, "(", 14);
		i.transition(14, ")", 15);
		i.finalState(15);
		i.subMachineCall(14, "expressao", 16);
		i.transition(16, ")", 15);
		i.transition(16, ",", 17);
		i.subMachineCall(17, "expressao", 16);
		
		
		i.machine("r_lvalue");
		i.finalState(1);
		i.transition(0, ".", 2);
		i.transition(1, ".", 2);
		i.transition(2, "id", 1);
		i.transition(0, "[", 3);
		i.transition(1, "[", 3);
		i.subMachineCall(3, "expressao", 4);
		i.transition(4, "]", 1);
		
		
		
		i.eof();
		
		
	}
	
	public static void inputDataVARFUNC(SubMachineInterpreter i) {
		int state;
		
		// programa = { declaracao_variavel | declaracao_tipo | funcao } .
		i.machine("programa");
		i.finalState(0);
		i.subMachineCall(0, "declaracao_variavel", 0);
		i.subMachineCall(0, "declaracao_tipo", 0);
		i.subMachineCall(0, "funcao", 0);
		
		// declaracao_variavel = "var" tipo id [ "=" ( constante | string ) ] { "," id [ "=" ( constante | string ) ] } ";" .
		state = 0;
		i.machine("declaracao_variavel");
		state = 0;
		i.transition(state, "var", 2);
		state = 2;
		i.subMachineCall(state, "tipo", 3);
		state = 3;
		i.transition(state, "id", 4);
		state = 4;
		i.transition(state, ";", 1);
		i.transition(state, "=", 5);
		i.transition(state, ",", 3);
		state = 5;
		i.subMachineCall(state, "expressao", 6);
		state = 6;
		i.transition(state, ";", 1);
		i.transition(state, ",", 3);
		i.finalState(1);
		
		// tipo = ( "int" | "char" | "type" id ) [ "[" "]" ] { "[" constante "]" } .
		i.machine("tipo");
		state = 0;
		i.transition(state, "int", 3);
		i.transition(state, "char", 3);
		i.transition(state, "type", 2);
		state = 2;
		i.transition(state, "id", 3);
		state = 3;
		i.finalState(state);
		i.transition(state, "[", 4);
		state = 4;
		i.transition(state, "]", 5);
		i.transition(state, "constante", 7);
		state = 5;
		i.finalState(state);
		i.transition(state, "[", 6);
		state = 6;
		i.transition(state, "constante", 7);
		state = 7;
		i.transition(state, "]", 5);
		
		// declaracao_tipo = "type" id "{" tipo id ";" { tipo id ";" } "}" ";" .
		i.machine("declaracao_tipo");
		state = 0;
		i.transition(state, "type", 2);
		state = 2;
		i.transition(state, "id", 3);
		state = 3;
		i.transition(state, "{", 5);
		state = 5;
		i.subMachineCall(state, "tipo", 6);
		state = 6;
		i.transition(state, "id", 7);
		state = 7;
		i.transition(state, ";", 8);
		state = 8;
		i.transition(state, "}", 9);
		i.subMachineCall(state, "tipo", 6);
		state = 9;
		i.transition(state, ";", 1);
		i.finalState(1);
		
		// funcao = "func" ( "int" | "char" | "void" ) id "(" [ tipo id { "," tipo id } ] ")" ( "{" { declaracao_variavel } { comando } "}" | ";" ) .
		i.machine("funcao");
		state = 0;
		i.transition(state, "func", 2);
		state = 2;
		i.transition(state, "int", 3);
		i.transition(state, "char", 3);
		i.transition(state, "void", 3);
		state = 3;
		i.transition(state, "id", 4);
		state = 4;
		i.transition(state, "(", 5);
		state = 5;
		i.transition(state, ")", 6); // continua
		i.subMachineCall(state, "tipo", 7);
		state = 7;
		i.transition(state, "id", 8);
		state = 8;
		i.transition(state, ")", 6);
		i.transition(state, ",", 9);
		state = 9;
		i.subMachineCall(state, "tipo", 7);
		state = 6;
		i.transition(state, ";", 1);
		i.transition(state, "{", 10);
		state = 10;
		i.subMachineCall(state, "declaracao_variavel", 10);
		i.transition(state, "}", 1);
		i.subMachineCall(state, "comando", 11);
		state = 11;
		i.subMachineCall(state, "comando", 11);
		i.transition(state, "}", 1);
		i.finalState(1);
		
		// comando = "{" { declaracao_variavel } { comando } "}" | expressao ";" | "if" "(" expressao ")" comando [ "else" comando ] | "while" "(" expressao ")" comando | "for" "(" expressao ";" expressao ";" expressao ")" comando | "return" expressao ";" | "continue" ";" | "break" ";" | ";" .
		i.machine("comando");
		state = 0;
		i.transition(state, "{", 2);
		i.subMachineCall(state, "expressao", 4);
		i.transition(state, "continue", 4);
		i.transition(state, "break", 4);
		i.transition(state, "return", 5);
		i.transition(state, ";", 1);
		i.transition(state, "if", 6);
		i.transition(state, "for", 12);
		i.transition(state, "while", 20);
		state = 2;
		i.subMachineCall(state, "declaracao_variavel", 2);
		i.transition(state, "}", 1);
		i.subMachineCall(state, "comando", 3);
		state = 3;
		i.subMachineCall(state, "comando", 3);
		i.transition(state, "}", 1);
		state = 4;
		i.transition(state, ";", 1);
		state = 5;
		i.subMachineCall(state, "expressao", 4);
		state = 6;
		i.transition(state, "(", 7);
		state = 7;
		i.subMachineCall(state, "expressao", 8);
		state = 8;
		i.transition(state, ")", 9);
		state = 9;
		i.subMachineCall(state, "comando", 10);
		state = 10;
		i.finalState(state);
		i.transition(state, "else", 11);
		state = 11;
		i.subMachineCall(state, "comando", 1);
		state = 12;
		i.transition(state, "(", 13);
		state = 13;
		i.subMachineCall(state, "expressao", 14);
		state = 14;
		i.transition(state, ";", 15);
		state = 15;
		i.subMachineCall(state, "expressao", 16);
		state = 16;
		i.transition(state, ";", 17);
		state = 17;
		i.subMachineCall(state, "expressao", 18);
		state = 18;
		i.transition(state, ")", 19);
		state = 19;
		i.subMachineCall(state, "comando", 1);
		state = 20;
		i.transition(state, "(", 21);
		state = 21;
		i.subMachineCall(state, "expressao", 22);
		state = 22;
		i.transition(state, ")", 23);
		state = 23;
		i.subMachineCall(state, "comando", 1);
		i.finalState(1);
		

		i.machine("expressao");
		String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&", "&&", "|", "||", "^", ">>", "<<" };
		String[] op1 = { "!", "~", "-" };
		
		i.transition(0, "constante", 3);
		i.transition(0, "string", 3);
		
		i.transition(0, "(", 1);
		i.subMachineCall(1, "expressao", 2);
		i.transition(2, ")", 3);
		
		i.finalState(3);
		
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
		i.finalState(7);
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
		i.finalState(12);
		i.transition(12, "=", 0);
		i.subMachineCall(12, "r_lvalue", 13);
		i.finalState(13);
		i.transition(13, "=", 0);
		
		for (String op : op2) {
			i.transition(12, op, 8);
			i.transition(13, op, 8);
		}
		
		i.transition(12, "(", 14);
		i.transition(14, ")", 15);
		i.finalState(15);
		i.subMachineCall(14, "expressao", 16);
		i.transition(16, ")", 15);
		i.transition(16, ",", 17);
		i.subMachineCall(17, "expressao", 16);
		
		
		i.machine("r_lvalue");
		i.finalState(1);
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
		private SymbolTable st;
		private Type type;
		
		private Calculation offset;
		private Symbol reference;
		private Symbol symbol;
		
		private TokenType first;
		private String id;
		private String typeId;
		
		private Type varType;
		
		private List<Integer> arraySizes = new ArrayList<Integer>();
		
		private int initilizer;
		

		public SemanticAction start = new SemanticActionWithToken() {
			public void doAction(Token token) {
				first = token.getType();
				id = typeId = null;
				arraySizes.clear();
				// inicializa tudo
			}
		};
		
		public SemanticAction setId = new SemanticActionWithToken() {
			public void doAction(Token token) {
				id = (String) token.getValue();
			}
		};
		
		public SemanticAction checkUnusedSymbol = new SemanticAction() {
			public void doAction(Object o) {
				if (st.containsSymbol(id)) error("duplicated definition of symbol " + id);
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
				if (!st.containsSymbol(typeId)) error("type is not defined");
				Symbol t = st.getSymbol(typeId);
				if (!(t instanceof CustomType)) error(typeId + " is not a type");
				varType = ((CustomType) t).getDefinition();
			}
		};
		
		public SemanticAction setVarType = new SemanticAction() {
			public void doAction(Object o) {
				switch (first) {
				case CHAR:
					varType = PrimitiveType.charTypeInstance();
					break;
				case INT:
					varType = PrimitiveType.intTypeInstance();
					break;
				case TYPE:
					// already set in this case
					break;
				}
				Collections.reverse(arraySizes);
				for (int i : arraySizes) {
					if (i == UNKOWN_SIZE) varType = new Array(varType);
					else varType = new SizedArray(varType, i);
				}
			}
		};
		
		public SemanticAction registerVariable = new SemanticAction() {
			public void doAction(Object result) {
				// TODO Auto-generated method stub
			}
		};
		
		public SemanticAction varInitializer = new SemanticAction() {
			public void doAction(Object o) {
				Calculation c = (Calculation) o;
				if (!c.isConstant()) error("initializer is not constant");
				initilizer = c.getValue();
				// TODO: parei aki!!
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
		
		public SemanticAction accessArrayElement = new SemanticAction() {
			public void doAction(Object obj) {
				type = ((Array) type).getInnerType();
				offset = Calculation.sum(offset, Calculation.multiply(type.sizeOf(), obj));
			}	
		};
		
		public SemanticAction accessRecordMember = new SemanticActionWithToken() {
			public void doAction(Token token) {
				Record r = (Record) type;
				String id = (String) token.getValue();
				
				if (!r.containsField(id))
					error(token, "field does not exist");
				
				type = r.getField(id).getType();
				offset = Calculation.sum(offset, r.getOffset(id));
			}
		};

		private void error(Token token, String text) {
			error("error on token: " + token.toString() + ": " + text);
		}

		private void error(String text) {
			throw new RuntimeException(text);
		}
		
	}
	
}
