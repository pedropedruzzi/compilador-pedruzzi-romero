package br.usp.pcs.compiler.symbol.type;

import java.util.Map;

public class Record extends Type {
	
	private final Field[] fields;
	private final int size;
	private Map<String, FieldData> map;

	public Record(Field[] fields) {
		this.fields = fields;
		int sum = 0;
		for (Field f : fields) {
			if (map.containsKey(f.getId())) throw new RuntimeException("duplicated field in record: " + f.getId());
			map.put(f.getId(), new FieldData(f, sum));
			sum += f.getType().sizeOf();
		}
		this.size = sum;
	}
	
	public boolean containsField(String id) {
		return map.containsKey(id);
	}
	
	public Field getField(String id) {
		return map.get(id).field;
	}
	
	public int getOffset(String id) {
		return map.get(id).offset;
	}

	public int sizeOf() {
		return size;
	}
	
	private static class FieldData {
		Field field;
		int offset;
		
		public FieldData(Field field, int offset) {
			this.field = field;
			this.offset = offset;
		}
	}

}
