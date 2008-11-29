package br.usp.pcs.compiler.memory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.usp.pcs.compiler.memory.Instruction.Opcode;

public class MemoryMap {

	private final String PREFIX = "__";
	private final String SUFIX = "_";
	private final String PREFIX_DATA = "data_";
	private int counter = 0;

	private Map<String, Allocation> map = new LinkedHashMap<String, Allocation>();
	private Map<String, String> pointers = new HashMap<String, String>();

	private String nextFreeSymbol() {
		return PREFIX + Integer.toString(counter++) + SUFIX;
	}

	public String allocVariable(String id, short initial) {
		String symbol = nextFreeSymbol() + id;
		checkSymbol(symbol);
		map.put(symbol, new SimpleAllocation(initial));
		return symbol;
	}

	private void checkSymbol(String symbol) {
		if (map.containsKey(symbol))
			throw new RuntimeException("name collision: " + symbol);
	}

	public String allocVariable() {
		return allocVariable(null, (short) 0);
	}

	public String allocVariable(String tag) {
		return allocVariable(tag, (short) 0);
	}

	public String allocPointer(String tag, String target) {
		String symbol = null;
		if (pointers.containsKey(target)) {
			symbol = pointers.get(target);
		} else {
			if (!map.containsKey(target)) throw new IllegalArgumentException("unkown target: " + target);
			symbol = nextFreeSymbol();
			if (tag != null) symbol = symbol + tag;
			checkSymbol(symbol);
			map.put(symbol, new PointerToSymbol(target));
			pointers.put(target, symbol);
		}
		return symbol;
	}

	public String allocPointedArea(String tag, int size) {
		String symbol = nextFreeSymbol();
		if (tag != null) symbol = symbol + tag;
		String symbolData = allocArea(PREFIX_DATA + tag, size);
		checkSymbol(symbol);
		map.put(symbol, new PointerToSymbol(symbolData));
		return symbol;
	}

	public String allocPointedArea(String tag, byte[] initial) {
		String symbol = nextFreeSymbol();
		if (tag != null) symbol = symbol + tag;
		String symbolData = allocArea(PREFIX_DATA + tag, initial);
		checkSymbol(symbol);
		map.put(symbol, new PointerToSymbol(symbolData));
		return symbol;
	}

	public String allocArea(String tag, int size) {
		String symbol = nextFreeSymbol();
		if (tag != null) symbol = symbol + tag;
		checkSymbol(symbol);
		map.put(symbol, new MemoryArea(size));
		return symbol;
	}

	public String allocArea(String tag, byte[] initial) {
		String symbol = nextFreeSymbol();
		if (tag != null) symbol = symbol + tag;
		checkSymbol(symbol);
		map.put(symbol, new InitializedMemoryArea(initial));
		return symbol;
	}

	public String label() {
		return label("label");
	}

	public String label(String tag) {
		String symbol = nextFreeSymbol();
		if (tag != null) symbol = symbol + tag;
		checkSymbol(symbol);
		map.put(symbol, new Label());
		return symbol;
	}
	
	public void generateCode(CodeBuffer cb) {
		for (String label : map.keySet())
			map.get(label).generateCode(label, cb);
	}

	private static abstract class Allocation {
		public abstract void generateCode(String label, CodeBuffer cb);
	}

	private static class SimpleAllocation extends Allocation {
		short initial;

		SimpleAllocation(short initial) {
			this.initial = initial;
		}

		public void generateCode(String label, CodeBuffer cb) {
			cb.addInstruction(new Instruction(label, Opcode.CONSTANT, initial));
		}
	}

	private static class PointerToSymbol extends Allocation {
		String symbol;

		public PointerToSymbol(String symbol) {
			this.symbol = symbol;
		}

		public void generateCode(String label, CodeBuffer cb) {
			cb.addInstruction(new Instruction(label, Opcode.CONSTANT, symbol));
		}
	}

	private static class MemoryArea extends Allocation {
		int size;

		public MemoryArea(int size) {
			this.size = size;
		}

		public void generateCode(String label, CodeBuffer cb) {
			cb.addInstruction(new Instruction(label, Opcode.BLOCK, size));
		}
	}

	private static class InitializedMemoryArea extends Allocation {
		byte[] initial;

		public InitializedMemoryArea(byte[] initial) {
			if ((initial.length & 1) != 0)
				this.initial = Arrays.copyOf(initial, initial.length + 1);
			else
				this.initial = initial;
		}

		public void generateCode(String label, CodeBuffer cb) {
			cb.addInstruction(new Instruction(label, Opcode.CONSTANT, (initial[0] << 8) | (initial[1] & 0xff)));
			for (int i = 2; i < initial.length; i += 2)
				cb.addInstruction(new Instruction(Opcode.CONSTANT, (initial[i] << 8) | (initial[i+1] & 0xff)));
		}
	}

	private static class Label extends Allocation {
		public void generateCode(String label, CodeBuffer cb) {
		}
	}

}