package br.usp.pcs.compiler.submachine;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Token.TokenType;
import br.usp.pcs.compiler.submachine.SubMachine.Transition;


public class SubMachineCreator implements SubMachineInterpreter {

	private SubMachine main;
	private Map<String, SubMachine> submachines = new HashMap<String, SubMachine>();
	private SubMachine current;
	private int currentState;
	private HashMap<TokenType, Transition> transitions;
	private Map<String, Set<String>> first;

	public SubMachineCreator(Map<String, Set<String>> firstMap) {
		first = firstMap;
	}

	@Override
	public void machine(String id) {
		current = getOrCreateSubMachine(id);
		if (main == null) main = current;
		assert current.transitions == null;
		current.transitions = new HashMap<Integer, Map<TokenType,Transition>>();
	}
	
	private SubMachine getOrCreateSubMachine(String id) {
		SubMachine sm = submachines.get(id);
		if (sm == null) {
			sm = new SubMachine(id);
			submachines.put(id, sm);
		}
		
		return sm;
	}

	@Override
	public void state(int num) {
		currentState = num;
		assert !current.transitions.containsKey(num) : "duplicated state definition";
		transitions = new HashMap<TokenType, Transition>();
		current.transitions.put(num, transitions);
	}

	@Override
	public void transition(String tokenType, int nextState) {
		TokenType tt = TokenType.getById(tokenType);
		if (tt == null) {
			throw new RuntimeException("unkown token class: " + tokenType);
		}
		
		assert !transitions.containsKey(tt) : "duplicated transition. " + current.getId() + ":" + currentState + ":" + tt.toString();
		transitions.put(tt, new Transition(nextState));
	}

	@Override
	public void subMachineCall(String subMachineId, int nextState) {
		SubMachine sm = getOrCreateSubMachine(subMachineId);
		for (String s : first.get(subMachineId)) {
			TokenType tt = TokenType.getById(s);
			if (tt == null) {
				throw new RuntimeException("unkown token class: " + s);
			}
			
			assert !transitions.containsKey(tt) : "duplicated transition. " + current.getId() + ":" + currentState + ":" + subMachineId;
			transitions.put(tt, new Transition(sm, nextState));
		}
	}
	
	@Override
	public void eof() {
		
	}
	
	public SubMachine getMainSubMachine() {
		return main;
	}

}
