package br.usp.pcs.compiler.submachine;
public interface SubMachineInterpreter {

	public void machine(String id);

	public void state(int num);

	public void finalState(int i);

	public void transition(String tokenType, int nextState);

	public void subMachineCall(String subMachineId, int nextState);
	
	// public void transition(String sm, int state, String token, int nextState);

	// public void subMachineCall(String sm, int state, String callee, int nextState);
	
	public void eof();

}