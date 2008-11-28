package br.usp.pcs.compiler.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InternalVariableManager {
	
	private final String TEMP_TIP = "temp";
	private final String CONST_TIP = "const";

	private List<String> free = new LinkedList<String>();
	private Map<Integer, String> constants = new HashMap<Integer, String>();
	private final MemoryMap mm;
	
	public InternalVariableManager(MemoryMap mm) {
		this.mm = mm;
	}
	
	public String takeTemporary() {
		if (free.isEmpty()) return mm.allocVariable(TEMP_TIP);
		else return free.remove(0);
	}
	
	public void returnTemporary(String var) {
		free.add(var);
	}
	
	public String getConstant(int value) {
		String ret = constants.get(value);
		if (ret == null) {
			ret = mm.allocVariable(CONST_TIP);
			constants.put(value, ret);
		}
		return ret;
	}
	
}
