package com.dtstack.util.key;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyUtils {

	@SuppressWarnings("unchecked")
	public static <K extends PrimaryKey, E extends PrimaryKeyGenerator> Map<K, E> convertListToMap(List<E> capacityList) {
		Map<PrimaryKey, PrimaryKeyGenerator> map = new  HashMap<PrimaryKey, PrimaryKeyGenerator>();
		for(PrimaryKeyGenerator generator : capacityList){
			map.put(generator.getPrimaryKey(), generator);
		}
		return (Map<K, E>) map;
	}
}
