package br.usp.pcs.compiler.submachine;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Token.TokenType;


public class SubMachineCreator implements SubMachineInterpreter {

	private SubMachine main;
	private Map<String, SubMachine> submachines = new HashMap<String, SubMachine>();
	private SubMachine current;
	private Map<String, Set<String>> first;

	public SubMachineCreator(Map<String, Set<String>> firstMap) {
		first = firstMap;
	}

	public void machine(String id) {
		current = getOrCreateSubMachine(id);
		if (main == null) main = current;
		assert current.transitions == null;
		current.transitions = new HashMap<Integer, Map<TokenType,Transition>>();
		current.finalStates = new HashMap<Integer, SubMachineReturnAction>();
	}
	
	private SubMachine getOrCreateSubMachine(String id) {
		SubMachine sm = submachines.get(id);
		if (sm == null) {
			sm = new SubMachine(id);
			submachines.put(id, sm);
		}
		
		return sm;
	}

	public void finalState(int num, SubMachineReturnAction smra) {
		current.finalStates.put(num, smra);
	}

	public void transition(int state, String tokenType, int next, SemanticAction ... sa) {
		TokenType tt = TokenType.getById(tokenType);
		if (tt == null) {
			throw new RuntimeException("unkown token class: " + tokenType);
		}
		
		Map<TokenType, Transition> map;
		if (!current.transitions.containsKey(state)) {
			map = new HashMap<TokenType, Transition>();
			current.transitions.put(state, map);
		} else {
			map = current.transitions.get(state);
			if (map.containsKey(tt)) throw new RuntimeException("duplicated transition. " + current.getId() + ":" + state + ":" + tt.toString());
		}
		map.put(tt, new Transition(next, sa));
	}

	public void subMachineCall(int state, String subMachineId, int next, SemanticAction ... sa) {
		SubMachine sm = getOrCreateSubMachine(subMachineId);
		for (String s : first.get(subMachineId)) {
			TokenType tt = TokenType.getById(s);
			if (tt == null) {
				throw new RuntimeException("unkown token class: " + s);
			}
			
			Map<TokenType, Transition> map;
			if (!current.transitions.containsKey(state)) {
				map = new HashMap<TokenType, Transition>();
				current.transitions.put(state, map);
			} else {
				map = current.transitions.get(state);
				if (map.containsKey(tt)) throw new RuntimeException("duplicated transition. " + current.getId() + ":" + state + ":" + tt.toString());
			}
			map.put(tt, new SubMachineCall(sm, next, sa));
		}
	}

	public void finalState(int num) {
		finalState(num, null);
	}
	
	public void eof() {
		
	}
	
	public SubMachine getMainSubMachine() {
		return main;
	}

}
