package br.usp.pcs.compiler.memory;

public class CompilationUnit {

	public final InternalVariableManager vm;
	public final CodeBuffer cb;
	public final MemoryMap mm;

	public CompilationUnit() {
		this.mm = new MemoryMap();
		this.cb = new CodeBuffer();
		this.vm = new InternalVariableManager(mm);
	}

	public CompilationUnit(MemoryMap mm, CodeBuffer cb, InternalVariableManager vm) {
		this.mm = mm;
		this.cb = cb;
		this.vm = vm;
	}

}
