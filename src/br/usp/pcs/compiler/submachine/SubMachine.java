package br.usp.pcs.compiler.submachine;

import java.util.Map;

import br.usp.pcs.compiler.CompilationException;
import br.usp.pcs.compiler.Lex;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;


public class SubMachine {
	
	private String id;
	
	Map<Integer, Map<TokenType, Transition>> transitions;
	Map<Integer, SubMachineReturnAction> finalStates;
	
	public SubMachine(String id) {
		this.id = id;
	}

	public Object execute(Lex lex) {
		int state = 0;
		Token token = null;
		try {
			while (lex.hasToken()) {
				token = lex.nextToken();
				// System.out.println(id + ": " + ": (" + state + ") " + token.toString());
				
				Transition t = null;
				if (transitions.containsKey(state))
					t = transitions.get(state).get(token.getType());
				
				if (t == null) {
					lex.giveBack(token);
					if (isFinal(state)) {
						SubMachineReturnAction action = getReturnAction(state);
						if (action == null) return true;
						else return action.returnAction();
					}
					else {
						System.err.println("Syntax error: unexpected token " + token.toString());
						return null;
					}
				}
				
				if (t instanceof SubMachineCall) {
					lex.giveBack(token);
					SubMachineCall smc = (SubMachineCall) t;
					Object result = smc.getSubMachine().execute(lex);
					if (result == null) return null;
	
					for (SemanticAction sa : t.getActions())
						sa.doAction(result);
				} else {
					for (SemanticAction sa : t.getActions())
						sa.doAction(token);
				}
				
				state = t.nextState;
			}
			if (isFinal(state)) {
				SubMachineReturnAction action = getReturnAction(state);
				if (action == null) return true;
				else return action.returnAction();
			} else {
				System.out.println("Syntax error: unexpected end of file.");
				return null;
			}
		} catch (CompilationException ce) {
			if (token != null) {
				System.err.println("Compilation exception on token: " + token.toString());
			}
			ce.printStackTrace();
			return null;
		}
	}
	
	private boolean isFinal(int state) {
		return finalStates.containsKey(state);
	}
	
	private SubMachineReturnAction getReturnAction(int state) {
		return finalStates.get(state);
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return id;
	}

	public void dumpTransitions() {
		System.out.println(transitions);
	}
	
}
