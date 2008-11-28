package br.usp.pcs.compiler.memory;

public class Instruction {

	private String label;
	private Opcode opcode;
	private String operand;
	
	public Instruction(String label, Opcode opcode, String operand) {
		this.label = label;
		this.opcode = opcode;
		this.operand = operand;
	}
	
	public Instruction(Opcode opcode, String operand) {
		this.opcode = opcode;
		this.operand = operand;
	}
	
	public Instruction(String label, Opcode opcode, int operand) {
		this.label = label;
		this.opcode = opcode;
		this.operand = format(operand);
	}
	
	public Instruction(Opcode opcode, int operand) {
		this.opcode = opcode;
		this.operand = format(operand);
	}
	
	private String format(int i) {
		if (tooBig(i)) throw new IllegalArgumentException("operand is too big");
		return '/' + Integer.toHexString(i);
	}
	
	private boolean tooBig(int i) {
		if (opcode.isPseudo()) {
			if (i != (i & 0xffff)) return true;
		} else {
			if (i != (i & 0xfff)) return true;
		}
		return false;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public String getOperand() {
		return operand;
	}
	
	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String toString() {
		return (label == null ? "\t\t" : label + "\t") + opcode.getMnemonic() + "\t" + operand;
	}
	

	public enum Opcode {
		JP, JZ, JN, LOAD_VALUE("LV"), ADD("+"), SUBTRACT("-"),
		MULTIPLY("*"), DIVIDE("/"), LOAD("LD"), STORE("MM"), CALL("SC"),
		RETURN("RS"), HALT("HM"), READ("GD"), WRITE("PD"), OS,
		ORIG("@"), END("#"), CONSTANT("K"), BLOCK("$");
		
		private final String mnemonic;

		private Opcode() {
			mnemonic = this.name();
		}
		
		private Opcode(String mnemonic) {
			this.mnemonic = mnemonic;
		}
		
		public String getMnemonic() {
			return mnemonic;
		}
		
		public boolean isPseudo() {
			return this == ORIG || this == END || this == CONSTANT || this == BLOCK;
		}
	}
	
}
