package com.tijiantest.model.item;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2014227370529021788L;
	private List<Conflict> value = new ArrayList<Conflict>();

	public ItemSelectException() {
	}

	public ItemSelectException(List<Conflict> conflictItems) {
		value = conflictItems;
	}
	
	public void addConflict(Integer itemId, ConflictType type) {
		Conflict c = new Conflict(itemId, type);
		this.value.add(c);
	}

	public List<Conflict> getValue() {
		return this.value;
	}

	public class Conflict {
		private Integer itemId;
		private ConflictType type;			

		public Conflict() {
			super();
		}

		public Conflict(Integer itemId, ConflictType type) {
			super();
			this.itemId = itemId;
			this.type = type;
		}

		public Integer getItemId() {
			return itemId;
		}

		public void setItemId(Integer itemId) {
			this.itemId = itemId;
		}

		public ConflictType getType() {
			return type;
		}

		public void setType(ConflictType type) {
			this.type = type;
		}
	}

	public enum ConflictType {
		COMPOSE(1, "合并"), CONFLICT(2, "冲突"), DEPEND(3, "依赖"), FAMILY(4, "父子"), DEPENDED(5, "被依赖"),GROUP(6,"同组");

		private String name;
		private int code;

		private ConflictType(int code, String name) {
			this.name = name;
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}
	}
}
