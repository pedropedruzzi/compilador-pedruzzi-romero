package br.usp.pcs.compiler.memory;
import java.util.HashMap;
import java.util.Map;

public class MemoryMap {

	private final String PREFIX = "__";
	private final String SUFIX = "_";
	private final String SUFIX_DATA = "_data";
	private int counter = 0;

	private Map<String, Allocation> map = new HashMap<String, Allocation>();

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

	public String allocVariable(String id) {
		return allocVariable(id, (short) 0);
	}

	public String allocArea(String id, int size) {
		String symbol = nextFreeSymbol() + id;
		String symbolData = symbol + SUFIX_DATA;
		checkSymbol(symbol);
		checkSymbol(symbolData);
		map.put(symbolData, new MemoryArea(size));
		map.put(symbol, new PointerToSymbol(symbolData));
		return symbol;
	}

	public String allocArea(String id, byte[] initial) {
		String symbol = nextFreeSymbol() + id;
		String symbolData = symbol + SUFIX_DATA;
		checkSymbol(symbol);
		checkSymbol(symbolData);
		map.put(symbolData, new InitializedMemoryArea(initial));
		map.put(symbol, new PointerToSymbol(symbolData));
		return symbol;
	}

	private static abstract class Allocation {
	}

	private static class SimpleAllocation extends Allocation {
		short initial;

		SimpleAllocation(short initial) {
			this.initial = initial;
		}
	}

	private static class PointerToSymbol extends Allocation {
		String symbol;

		public PointerToSymbol(String symbol) {
			this.symbol = symbol;
		}
	}

	private static class MemoryArea extends Allocation {
		int size;

		public MemoryArea(int size) {
			this.size = size;
		}
	}

	private static class InitializedMemoryArea extends Allocation {
		byte[] initial;

		public InitializedMemoryArea(byte[] initial) {
			this.initial = initial;
		}
	}

}