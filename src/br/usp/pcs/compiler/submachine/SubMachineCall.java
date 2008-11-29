package br.usp.pcs.compiler.submachine;

public class SubMachineCall extends Transition {
	
	protected SubMachine subMachine;
	
	public SubMachineCall(SubMachine subMachine, int nextState) {
		super(nextState);
		this.subMachine = subMachine;
	}
	
	public SubMachineCall(SubMachine subMachine, int nextState, SemanticAction ... actions) {
		super(nextState, actions);
		this.subMachine = subMachine;
	}
	
	public SubMachine getSubMachine() {
		return subMachine;
	}
	
	public String toString() {
		return super.toString() + " " + subMachine.getId();
	}
	
}
