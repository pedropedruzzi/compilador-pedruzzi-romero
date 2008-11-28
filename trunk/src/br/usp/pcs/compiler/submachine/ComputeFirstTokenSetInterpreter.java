package br.usp.pcs.compiler.submachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

}
