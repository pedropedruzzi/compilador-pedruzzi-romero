package br.usp.pcs.compiler;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import mvn.montador.TwoPassAssembler;
import br.usp.pcs.compiler.memory.CompilationUnit;
import br.usp.pcs.compiler.semantic.Semantic;
import br.usp.pcs.compiler.submachine.ComputeFirstTokenSetInterpreter;
import br.usp.pcs.compiler.submachine.SubMachine;
import br.usp.pcs.compiler.submachine.SubMachineCreator;
import br.usp.pcs.compiler.submachine.SubMachineInterpreter;

public class Main {
	
	private SubMachine S;
	private CompilationUnit cu = new CompilationUnit();
	private Semantic semantic = new Semantic(cu);
	
	public Main() {
		generateSubMachines();
	}
	
	private void generateSubMachines() {
		ComputeFirstTokenSetInterpreter fti = new ComputeFirstTokenSetInterpreter();
		inputData(fti);
		
		// dump first
		System.out.println(fti.getFirstMap());
		
		SubMachineCreator smc = new SubMachineCreator(fti.getFirstMap());
		inputData(smc);
		S = smc.getMainSubMachine();
		
		// dump transitions
		S.dumpTransitions();
	}

	public static void main(String[] args) throws FileNotFoundException {
		Main main = new Main();
		main.compile("res/teste2.c", "out/teste2.asm");
		main.assembly("out/teste2.asm", "out/teste2.obj", "out/teste2.lst");
	}
	
	private boolean compile(String inputfile, String outputfile) throws FileNotFoundException {
		Lex lex = new LexicalParser(inputfile);
		//while (lex.hasToken()) System.out.println(lex.nextToken());
		
		if (S.execute(lex) == null) return false;
		
		// compilation ok if there is no more tokens left
		if (lex.hasToken()) {
			System.err.println("Syntax error: unexpected token " + lex.nextToken().toString());
			return false;
		}
		
		// check for programming errors!
		semantic.checkEnd();
		
		cu.mm.generateCode(cu.cb);
		cu.cb.write(System.out);
		
		PrintStream ps = new PrintStream(outputfile);
		cu.cb.write(ps);
		ps.close();
		
		return true;
	}
	
	private void assembly(String fileName, String objFileName, String listFileName) {
		TwoPassAssembler tpa = new TwoPassAssembler(fileName, objFileName, listFileName);
		tpa.assemble();
	}

	public void inputData(SubMachineInterpreter i) {
		i.machine("programa");
		i.finalState(0);
		i.transition(0, "int", 1, semantic.start, semantic.setType);
		i.transition(0, "char", 1, semantic.start, semantic.setType);
		
		i.transition(1, "id", 2, semantic.setId);
		i.subMachineCall(2, "r_funcao", 0);
		i.subMachineCall(2, "r_declaracao_variavel", 0);
		
		i.transition(1, "[", 8); // variavel
		i.transition(8, "constante", 10, semantic.arraySize);
		i.transition(8, "]", 11, semantic.arraySize);
		i.transition(10, "]", 11);
		i.transition(11, "[", 9);
		i.transition(9, "constante", 10, semantic.arraySize);
		i.transition(11, "id", 12, semantic.setArrayType, semantic.setId);
		i.subMachineCall(12, "r_declaracao_variavel", 0);
		
		i.transition(0, "void", 3, semantic.start); //funcao
		i.transition(3, "id", 4, semantic.setId);
		i.subMachineCall(4, "r_funcao", 0);
		
		i.transition(0, "type", 5, semantic.start);
		i.transition(5, "id", 6, semantic.setTypeId);
		
		i.subMachineCall(6, "r_declaracao_tipo", 0, semantic.registerType); // definicao de tipo

		i.transition(6, "[", 8, semantic.checkType); // variavel
		
		i.transition(6, "id", 7, semantic.checkType, semantic.setId); // variavel
		i.subMachineCall(7, "r_declaracao_variavel", 0);
		
		// r_declaracao_variavel = [ "=" expressao ] { "," id [ "=" expressao ] } ";" .
		i.machine("r_declaracao_variavel");
		i.transition(0, ";", 1, semantic.registerVariable);
		i.transition(0, "=", 2);
		i.transition(0, ",", 4, semantic.registerVariable);
		i.subMachineCall(2, "expressao", 3, semantic.varInitializer);
		i.transition(3, ";", 1, semantic.registerVariable);
		i.transition(3, ",", 4, semantic.registerVariable);
		i.transition(4, "id", 0, semantic.setId);
		i.finalState(1);
		
		// declaracao_variavel = tipo id r_declaracao_variavel .
		i.machine("declaracao_variavel");
		i.subMachineCall(0, "tipo", 2);
		i.transition(2, "id", 3, semantic.setId);
		i.subMachineCall(3, "r_declaracao_variavel", 1);
		i.finalState(1);
		
		// tipo = ( "int" | "char" | "type" id ) [ "[" "]" ] { "[" constante "]" } .
		i.machine("tipo");
		i.transition(0, "int", 3, semantic.setType);
		i.transition(0, "char", 3, semantic.setType);
		i.transition(0, "type", 2);
		i.transition(2, "id", 3, semantic.setTypeId, semantic.checkType);
		i.finalState(3);
		i.transition(3, "[", 4);
		i.transition(4, "]", 5, semantic.arraySize);
		i.transition(4, "constante", 7, semantic.arraySize);
		i.finalState(5, semantic.setArrayTypeAndReturn);
		i.transition(5, "[", 6);
		i.transition(6, "constante", 7, semantic.arraySize);
		i.transition(7, "]", 5);
		
		// r_declaracao_tipo = "{" tipo id ";" { tipo id ";" } "}" ";" .
		i.machine("r_declaracao_tipo");
		i.transition(0, "{", 2, semantic.typeDefinition, semantic.checkDuplicatedType);
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
		i.transition(0, "(", 5, semantic.checkDuplicatedFunction, semantic.functionStart);
		i.transition(5, ")", 6);
		i.subMachineCall(5, "tipo", 7);
		i.transition(7, "id", 8, semantic.functionArgumentDeclaration);
		i.transition(8, ")", 6);
		i.transition(8, ",", 9);
		i.subMachineCall(9, "tipo", 7);
		i.transition(6, ";", 1);
		i.transition(6, "{", 10, semantic.registerFunction, semantic.newCommandContext);
		i.subMachineCall(10, "declaracao_variavel", 10);
		i.transition(10, "}", 1, semantic.returnVoid, semantic.destroyCommandContext, semantic.destroyScope);
		i.subMachineCall(10, "comando", 11);
		i.subMachineCall(11, "comando", 11);
		i.transition(11, "}", 1, semantic.returnVoid, semantic.destroyCommandContext, semantic.destroyScope);
		i.finalState(1);
		
		// comando = "{" { declaracao_variavel } { comando } "}" | expressao ";" | "if" "(" expressao ")" comando [ "else" comando ] | "while" "(" expressao ")" comando | "for" "(" expressao ";" expressao ";" expressao ")" comando | "return" expressao ";" | "continue" ";" | "break" ";" | ";" .
		i.machine("comando");
		i.transition(0, "{", 2, semantic.openNewScope, semantic.newCommandContext);
		i.subMachineCall(0, "expressao", 4, semantic.evaluateE);
		i.transition(0, "continue", 4, semantic.continueLoop);
		i.transition(0, "break", 4, semantic.breakLoop);
		i.transition(0, "return", 5);
		i.transition(0, ";", 1);
		i.transition(0, "if", 6);
		i.transition(0, "for", 12);
		i.transition(0, "while", 20);
		i.subMachineCall(2, "declaracao_variavel", 2);
		i.transition(2, "}", 1, semantic.destroyScope);
		i.subMachineCall(2, "comando", 3);
		i.subMachineCall(3, "comando", 3);
		i.transition(3, "}", 1, semantic.destroyScope, semantic.destroyCommandContext);
		i.transition(4, ";", 1);
		i.subMachineCall(5, "expressao", 4, semantic.returnE);
		i.transition(6, "(", 7);
		i.subMachineCall(7, "expressao", 8, semantic.ifE);
		i.transition(8, ")", 9, semantic.newCommandContext);
		i.subMachineCall(9, "comando", 10, semantic.destroyCommandContext);
		i.finalState(10, semantic.noElse);
		i.transition(10, "else", 11, semantic.hasElse, semantic.newCommandContext);
		i.subMachineCall(11, "comando", 1, semantic.destroyCommandContext, semantic.endIf);
		i.transition(12, "(", 13);
		i.subMachineCall(13, "expressao", 14, semantic.forE1);
		i.transition(14, ";", 15);
		i.subMachineCall(15, "expressao", 16, semantic.forE2);
		i.transition(16, ";", 17);
		i.subMachineCall(17, "expressao", 18, semantic.forE3);
		i.transition(18, ")", 19, semantic.newCommandContext);
		i.subMachineCall(19, "comando", 1, semantic.destroyCommandContext, semantic.endFor);
		i.transition(20, "(", 21);
		i.subMachineCall(21, "expressao", 22, semantic.whileE);
		i.transition(22, ")", 23, semantic.newCommandContext);
		i.subMachineCall(23, "comando", 1, semantic.destroyCommandContext, semantic.endWhile);
		i.finalState(1);
		

		i.machine("expressao");
		// String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&", "&&", "|", "||", "^", ">>", "<<" };
		// String[] op1 = { "!", "~", "-" };
		String[] op2 = { "+", "-", "*", "/", "%", "!=", "==", "<", "<=", ">", ">=", "&&", "||" };
		String[] op1 = { "!", "-" };
		
		i.transition(0, "constante", 3, semantic.constant);
		i.transition(0, "string", 3, semantic.string);
		
		i.transition(0, "(", 1, semantic.newExpressionContext);
		i.subMachineCall(1, "expressao", 2, semantic.destroyExpressionContext, semantic.subExpression);
		i.transition(2, ")", 3);
		
		i.finalState(3, semantic.endE);
		
		for (String op : op1) {
			i.transition(0, op, 4, semantic.op1);
			i.transition(4, op, 4, semantic.op1);
		}
		
		// 4 ===e3==> 3

		i.transition(4, "constante", 3, semantic.constant);
		i.transition(4, "string", 3, semantic.string);
		
		i.transition(4, "(", 5, semantic.newExpressionContext);
		i.subMachineCall(5, "expressao", 6, semantic.destroyExpressionContext, semantic.subExpression);
		i.transition(6, ")", 3);
		
		i.transition(4, "id", 7);
		i.finalState(7, semantic.endE);
		for (String op : op2)
			i.transition(7, op, 8, semantic.op2);
		
		i.subMachineCall(7, "r_lvalue", 3);
		i.transition(7, "(", 9);
		i.transition(9, ")", 3);
		i.subMachineCall(9, "expressao", 10);
		i.transition(10, ")", 3);
		i.transition(10, ",", 11);
		i.subMachineCall(11, "expressao", 10);
		
		// FIM
		
		for (String op : op2)
			i.transition(3, op, 8, semantic.op2);
		
		for (String op : op1)
			i.transition(8, op, 8, semantic.op1);
		
		// 8 ===e3==> 3

		i.transition(8, "constante", 3, semantic.constant);
		i.transition(8, "string", 3, semantic.string);
		i.transition(8, "(", 5);
		i.transition(8, "id", 7, semantic.setIdE);
		
		// FIM
		
		i.transition(0, "id", 12, semantic.setIdE);
		i.finalState(12, semantic.endE);
		i.transition(12, "=", 0, semantic.checkVar, semantic.lValueEnd, semantic.assignment);
		i.subMachineCall(12, "r_lvalue", 13, semantic.lValueEnd);
		i.finalState(13, semantic.endE);
		i.transition(13, "=", 0, semantic.assignment);
		
		for (String op : op2) {
			i.transition(12, op, 8, semantic.checkVar, semantic.lValueEnd, semantic.op2);
			i.transition(13, op, 8, semantic.op2);
		}
		
		i.transition(12, "(", 14, semantic.checkFunction);
		i.transition(14, ")", 3);
		i.subMachineCall(14, "expressao", 16, semantic.functionArgument);
		i.transition(16, ")", 3, semantic.functionCallEnd);
		i.transition(16, ",", 17);
		i.subMachineCall(17, "expressao", 16, semantic.functionArgument);
		
		
		i.machine("r_lvalue");
		i.finalState(1, semantic.endE);
		i.transition(0, ".", 2, semantic.checkVar);
		i.transition(1, ".", 2);
		i.transition(2, "id", 1, semantic.accessRecordMember);
		i.transition(0, "[", 3, semantic.checkVar);
		i.transition(1, "[", 3);
		i.subMachineCall(3, "expressao", 4, semantic.accessArrayElement);
		i.transition(4, "]", 1);
		
		
		
		i.eof();
	}
	
}
