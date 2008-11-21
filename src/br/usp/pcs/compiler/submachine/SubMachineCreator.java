package br.usp.pcs.compiler.submachine;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.usp.pcs.compiler.Token.TokenType;
import br.usp.pcs.compiler.submachine.SubMachine.Transition;


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
		current.finalStates = new HashSet<Integer>();
	}
	
	private SubMachine getOrCreateSubMachine(String id) {
		SubMachine sm = submachines.get(id);
		if (sm == null) {
			sm = new SubMachine(id);
			submachines.put(id, sm);
		}
		
		return sm;
	}

	public void finalState(int num) {
		current.finalStates.add(num);
	}

	public void transition(int state, String tokenType, int next) {
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
		map.put(tt, new Transition(next));
	}

	public void subMachineCall(int state, String subMachineId, int next) {
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
			map.put(tt, new Transition(sm, next));
		}
	}
	
	public void eof() {
		
	}
	
	public SubMachine getMainSubMachine() {
		return main;
	}

}
