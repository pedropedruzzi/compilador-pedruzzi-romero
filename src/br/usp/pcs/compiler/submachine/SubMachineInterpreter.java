package br.usp.pcs.compiler.submachine;

public interface SubMachineInterpreter {

	public void machine(String id);

	public void finalState(int i);

	public void finalState(int i, SubMachineReturnAction smra);

	public void transition(int state, String tokenType, int next, SemanticAction ... sa);

	public void subMachineCall(int state, String subMachineId, int next, SemanticAction ... sa);
	
	public void eof();

}