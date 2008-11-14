package br.usp.pcs.compiler.submachine;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Lex;
import br.usp.pcs.compiler.LexicalParser;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;


public class ComputeFirstTokenSetInterpreter implements SubMachineInterpreter {
	
	// set of interested submachines
	private Set<String> interested = new HashSet<String>();
	private Map<String, Set<String>> firstToken = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> firstSM = new HashMap<String, Set<String>>();
	
	private Map<String, Set<String>> finalFirst;
	
	private String currentMachine;
	private boolean firstState;

	/* (non-Javadoc)
	 * @see SubMachineInterpreter#machine(java.lang.String)
	 */
	public void machine(String id) {
		currentMachine = id;
		firstState = false;
		firstToken.put(id, new HashSet<String>());
		firstSM.put(id, new HashSet<String>());
	}
	
	/* (non-Javadoc)
	 * @see SubMachineInterpreter#state(int)
	 */
	public void state(int num) {
		firstState = (num == 0);
	}
	
	/* (non-Javadoc)
	 * @see SubMachineInterpreter#transition(java.lang.String, int)
	 */
	public void transition(String tokenType, int nextState) {
		if (firstState) {
			firstToken.get(currentMachine).add(tokenType);
		}
	}
	
	/* (non-Javadoc)
	 * @see SubMachineInterpreter#subMachineCall(java.lang.String, int)
	 */
	public void subMachineCall(String subMachineId, int nextState) {
		interested.add(subMachineId);
		if (firstState) {
			firstSM.get(currentMachine).add(subMachineId);
		}
	}

	@Override
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
			finalFirst.put(subMachineId, first);
		}
	}
	
	public Map<String, Set<String>> getFirstMap() {
		return finalFirst;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		ComputeFirstTokenSetInterpreter fti = new ComputeFirstTokenSetInterpreter();
		inputData(fti);
		System.out.println(fti.getFirstMap());
		SubMachineCreator smc = new SubMachineCreator(fti.getFirstMap());
		inputData(smc);
		SubMachine S = smc.getMainSubMachine();
		
		System.out.println(S.transitions);
		//System.exit(0);
		
		Lex lex = new LexicalParser("res/teste2.c");
		//while (lex.hasToken()) System.out.println(lex.nextToken());
		
		System.out.println("resultados = " + S.execute(lex, new SemanticActionManager() {

			@Override
			public void processTransition(String subMachineId, int state,
					TokenType tokenType, Token token) {
				//System.out.println(subMachineId + ": (" + state + "," + tokenType.toString() + ")");
			}
			
		}));
	}
	
	public static void inputData(SubMachineInterpreter i) {
		// comando = 0 "{" 2 { 3 comando 4 } 3 "}" 5 | 0 expressao 6 ";" 7 | 0 "if" 8 "(" 9 expressao 10 ")" 11 comando 12 "else" 13 comando 14 | 0 "while" 15 "(" 16 expressao 17 ")" 18 comando 19 | 0 "for" 20 "(" 21 expressao 22 ";" 23 expressao 24 ";" 25 expressao 26 ")" 27 comando 28 | 0 "return" 29 expressao 30 . 1MOD
		// expressao = 0 ( 0 "INTEGER_LITERAL" 3 | 0 "IDENTIFIER" 4 ) 2 { 5 "+" 6 ( 6 "INTEGER_LITERAL" 8 | 6 "IDENTIFIER" 9 ) 7 } 5 . 1 MOD
		// termo = "INTEGER_LITERAL" | "IDENTIFIER" .
		
		i.machine("comando");
		i.state(0);
		i.transition("{", 2);
		i.subMachineCall("expressao", 6);
		i.transition("if", 8);
		i.transition("while", 15);
		i.transition("for", 20);
		i.transition("return", 29);
		i.state(2);
		i.subMachineCall("comando", 2);
		i.transition("}", 1);
		i.state(6);
		i.transition(";", 1);
		
		i.state(8);
		i.transition("(", 9);
		i.state(9);
		i.subMachineCall("expressao", 10);
		i.state(10);
		i.transition(")", 11);
		i.state(11);
		i.subMachineCall("comando", 12);
		i.state(12);
		i.transition("else", 13);
		
		i.state(13);
		i.subMachineCall("comando", 1);
		i.state(15);
		i.transition("(", 16);
		i.state(16);
		i.subMachineCall("expressao", 17);
		i.state(17);
		i.transition(")", 18);
		i.state(18);
		i.subMachineCall("comando", 1);
		
		i.state(20);
		i.transition("(", 21);
		i.state(21);
		i.subMachineCall("expressao", 22);
		i.state(22);
		i.transition(";", 23);
		i.state(23);
		i.subMachineCall("expressao", 24);
		i.state(24);
		i.transition(";", 25);
		i.state(25);
		i.subMachineCall("expressao", 26);
		i.state(26);
		i.transition(")", 27);
		i.state(27);
		i.subMachineCall("comando", 1);
		
		i.state(29);
		i.subMachineCall("expressao", 30);
		i.state(30);
		i.transition(";", 1);
		i.state(1);
		


		i.machine("expressao");
		i.state(0);
		i.transition("num", 1);
		i.transition("id", 1);
		i.state(1);
		i.transition("+", 2);
		i.state(2);
		i.transition("num", 1);
		i.transition("id", 1);
		
		i.eof();
		
	}
	
	public static void inputDataPetros(SubMachineInterpreter i) {
		// programa-petros = "PROGRAMA" identificador declaracoes bloco-comandos.
		i.machine("programa-petros");
		i.state(0);
		i.transition("PROGRAMA", 2);
		i.state(2);
		i.transition("ID", 3);
		i.state(3);
		i.subMachineCall("declaracoes", 4);
		i.state(4);
		i.subMachineCall("bloco-comandos", 1);
	}
	
}
