package br.usp.pcs.compiler.submachine;
public interface SubMachineInterpreter {

	public void machine(String id);

	public void finalState(int i);

	public void transition(int state, String tokenType, int next);

	public void subMachineCall(int state, String subMachineId, int next);
	
	public void eof();

}