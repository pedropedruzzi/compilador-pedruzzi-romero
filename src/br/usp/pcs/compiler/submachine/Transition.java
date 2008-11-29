package br.usp.pcs.compiler.submachine;

public class Transition {
	
	protected int nextState;
	protected SemanticAction[] actions;
	
	public Transition(int nextState) {
		this.nextState = nextState;
	}
	
	public Transition(int nextState, SemanticAction ... actions) {
		this.nextState = nextState;
		this.actions = actions;
	}
	
	public int getNextState() {
		return nextState;
	}
	
	public SemanticAction[] getActions() {
		return actions;
	}
	
	public String toString() {
		return "(" + nextState + ")";
	}
	
}
