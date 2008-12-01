package br.usp.pcs.compiler.memory;

import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class CompilationUnit {

	public final InternalVariableManager vm;
	public final CodeBuffer cb;
	public final MemoryMap mm;

	public CompilationUnit() {
		this.mm = new MemoryMap();
		this.cb = new CodeBuffer();
		this.vm = new InternalVariableManager(mm);

		cb.addInstruction(new Instruction(Opcode.CALL, "main"));
		cb.addInstruction(new Instruction(Opcode.HALT, 0));
	}

	public CompilationUnit(MemoryMap mm, CodeBuffer cb, InternalVariableManager vm) {
		this.mm = mm;
		this.cb = cb;
		this.vm = vm;
	}

}
