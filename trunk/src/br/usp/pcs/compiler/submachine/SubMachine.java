package br.usp.pcs.compiler.submachine;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Lex;
import br.usp.pcs.compiler.Token;
import br.usp.pcs.compiler.Token.TokenType;


public class SubMachine {

	//private Lex lex;
	//private SemanticActionManager sam;
	private String id;
	
	Map<Integer, Map<TokenType, Transition>> transitions;
	Set<Integer> finalStates;
	
	public SubMachine(String id) {
		this.id = id;
	}

	public boolean execute(Lex lex, SemanticActionManager sam) {
		int state = 0;
		while (lex.hasToken()) {
			Token token = lex.nextToken();
			System.out.println(id + ": " + ": (" + state + ") " + token.toString());
			
			Transition t = null;
			if (transitions.containsKey(state))
				t = transitions.get(state).get(token.getType());
			
			if (t == null) {
				lex.giveBack(token);
				if (isFinal(state)) return true;
				else {
					System.out.println("Syntax error: unexpected token " + token.toString());
					return false;
				}
			}
			if (t.subMachine != null) {
				// submachine call
				lex.giveBack(token);
				if (!t.subMachine.execute(lex, sam)) return false;
			} else {
				// simple transition
				sam.processTransition(id, state, token.getType(), token);
			}
			state = t.nextState;
		}
		if (isFinal(state)) return true;
		else {
			System.out.println("Syntax error: unexpected end of file.");
			return false;
		}
	}
	
	private boolean isFinal(int state) {
		return finalStates.contains(state);
	}

	public static class Transition {
		int nextState;
		SubMachine subMachine;
		
		public Transition(int nextState) {
			this.nextState = nextState;
		}
		
		public Transition(SubMachine subMachine, int nextState) {
			this.subMachine = subMachine;
			this.nextState = nextState;
		}
		
		@Override
		public String toString() {
			return "(" + nextState + (subMachine != null ? ") " + subMachine.getId() : ")");
		}
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id;
	}
	
}
