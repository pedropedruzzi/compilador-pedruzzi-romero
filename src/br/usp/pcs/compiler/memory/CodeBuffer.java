package br.usp.pcs.compiler.memory;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CodeBuffer {

	private Map<String, String> translation = new HashMap<String, String>();
	private List<Instruction> buffer = new LinkedList<Instruction>();
	private String nextLabel;
	
	public void addInstruction(Instruction i) {
		if (nextLabel != null) {
			if (i.getLabel() == null) {
				i.setLabel(nextLabel);
				nextLabel = null;
			} else {
				throw new IllegalStateException("already set");
			}
		}
		buffer.add(i);
	}
	
	public String getNextLabel() {
		return nextLabel;
	}
	
	public void setNextLabel(String nextLabel) {
		if (this.nextLabel != null) translation.put(nextLabel, this.nextLabel);
		else this.nextLabel = nextLabel;
	}
	
	private void translate() {
		for (Instruction i : buffer) {
			String n = translation.get(i.getLabel());
			if (n != null) i.setLabel(n);
			n = translation.get(i.getOperand());
			if (n != null) i.setOperand(n);
		}
	}

	public void write(PrintStream out) {
		translate();
		for (Instruction i : buffer) out.println(i.toString());
	}
	
}
