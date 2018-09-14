package com.tijiantest.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.tijiantest.util.exception.ExceptionWithCode;

public class AssertUtil {

	public static <T> void notNull(T obj) {
		notNull(obj, "参数值不能为空.");
	}

	public static <T> void notNull(T obj, String msg) {
		if (isNull(obj)) {
			throw new AssertException(AssertException.IS_NULL, msg);
		}
	}

	public static <T, E extends Exception> void notNull(T obj, E e) throws E {
		if (isNull(obj)) {
			throw e;
		}
	}

	public static <T> boolean isNotNull(T obj) {
		return !isNull(obj);
	}

	public static <T> boolean isNull(T obj) {
		return obj == null;
	}

	public static void notEmpty(String str) {
		notEmpty(str, "参数值不能为空");
	}

	public static void notEmpty(String str, String msg) {
		if (isEmpty(str)) {
			throw new AssertException(AssertException.IS_EMPTY, msg);
		}
	}

	public static <E extends Exception> void notEmpty(String str, E e) throws E {
		if (isEmpty(str)) {
			throw e;
		}
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0);
	}
	
	public static <T, V> void equals(T obj1, V obj2) {
		notEquals(obj1, obj2, "对象值不能相等");
	}
	
	public static <T, V> void equals(T obj1, V obj2, String msg) {
		if (areEquals(obj1, obj2)) {
			throw new AssertException(AssertException.ARE_EQUAL, msg);
		}
	}
	
	public static <T, V, E extends Exception> void equals(T obj1, V obj2, E e) throws E {
		if (areEquals(obj1, obj2)) {
			throw e;
		}
	}

	public static <T, V> void notEquals(T obj1, V obj2) {
		notEquals(obj1, obj2, "对象值不相等");
	}

	public static <T, V> void notEquals(T obj1, V obj2, String msg) {
		if (areNotEquals(obj1, obj2)) {
			throw new AssertException(AssertException.ARE_NOT_EQUAL, msg);
		}
	}

	public static <T, V, E extends Exception> void notEquals(T obj1, V obj2, E e) throws E {
		if (areNotEquals(obj1, obj2)) {
			throw e;
		}
	}
	
	public static <T, V> boolean areNotEquals(T obj1, V obj2) {
		return !areEquals(obj1, obj2);
	}

	public static <T, V> boolean areEquals(T obj1, V obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}

		if ((obj1 == null && obj2 != null) || (obj1 != null && obj2 == null)) {
			return false;
		}

		return obj1.equals(obj2);
	}

	public static <T> void notEmpty(T[] array) {
		notEmpty(array, "数组不能为空");
	}

	public static <T> void notEmpty(T[] array, String msg) {
		if (isEmpty(array)) {
			throw new AssertException(AssertException.IS_EMPTY, msg);
		}
	}

	public static <T, E extends Exception> void notEmpty(T[] array, E e) throws E {
		if (isEmpty(array)) {
			throw e;
		}
	}

	public static <T> boolean isNotEmpty(T[] array) {
		return !isEmpty(array);
	}

	public static <T> boolean isEmpty(T[] array) {
		return (array == null || array.length == 0);
	}

	public static void notEmpty(byte[] array) {
		notEmpty(array, "数组不能为空");
	}

	public static void notEmpty(byte[] array, String msg) {
		if (isEmpty(array)) {
			throw new AssertException(AssertException.IS_EMPTY, msg);
		}
	}

	public static <E extends Exception> void notEmpty(byte[] array, E e) throws E {
		if (isEmpty(array)) {
			throw e;
		}
	}

	public static boolean isNotEmpty(byte[] array) {
		return !isEmpty(array);
	}

	public static boolean isEmpty(byte[] array) {
		return (array == null || array.length == 0);
	}

	public static void notEmpty(Collection<?> collection) {
		notEmpty(collection, "集合不能为空");
	}

	public static void notEmpty(Collection<?> collection, String msg) {
		if (isEmpty(collection)) {
			throw new AssertException(AssertException.IS_EMPTY, msg);
		}
	}

	public static <E extends Exception> void notEmpty(Collection<?> collection, E e) throws E {
		if (isEmpty(collection)) {
			throw e;
		}
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}
	
	public static boolean equalList(Collection<Integer> list1, Collection<Integer> list2) {
		if (isEmpty(list1) && isEmpty(list2)) {
			return true;
		}
		if (isEmpty(list1) || isEmpty(list2)) {
			return false;
		}
		if (list1.size() != list2.size()) {
			return false;
		}
		
		return list1.stream().filter(e -> ! list2.contains(e)).count() == 0;
	}

	public static boolean equalInteger(Integer price, Integer price2) {
		if (price == null && price2 == null) {
			return true;
		}
		if (price == null || price2 == null) {
			return false;
		}
		return price.intValue() == price2.intValue();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> void notNulls( String msg,T... objs) {
		for(T t:objs) {
			if (isNull(t)) {
				throw new AssertException(AssertException.IS_NULL, msg);
			}

		}
	}
	
	public static <T> void gt_Int(Integer a,Integer b, String msg) {
		if(a.intValue()<b.intValue()) {
			throw new AssertException(AssertException.ARE_NOT_LT, msg);
		}
	}


	static class AssertException extends IllegalArgumentException implements ExceptionWithCode {

		private static final long serialVersionUID = 1L;

		public static final int IS_NULL = 1;

		public static final int IS_FALSE = 2;

		public static final int ARE_EQUAL = 3;

		public static final int IS_EMPTY = 4;
		
		public static final int ARE_NOT_EQUAL = 5;
		
		public static final int ARE_NOT_LT = 6;

		private int code;

		AssertException(int code, String message) {
			super(message);
			this.code = code;
		}

		@Override
		public int getCode() {
			return code;
		}

	}

	/**
	 * 比较两个相同类的对象
	 * @param actualObj
	 * @param expectObj
	 * @param ignoreFields 不参与验证的变量
	 * @return
	 * @throws IllegalAccessException
	 */
	public static boolean equalObject(Object actualObj,Object expectObj,String ...ignoreFields) throws IllegalAccessException {
		List<String> ignoreFieldList = Arrays.asList(ignoreFields);
		Class<?> clazz = expectObj.getClass();
		Field[] expectFields = clazz.getDeclaredFields();
		for(Field expectField : expectFields){
			if(ignoreFieldList.size()>0&&ignoreFieldList.contains(expectField.getName())){
				continue;
			}
			Field actualField = null;
			try {
				actualField = clazz.getDeclaredField(expectField.getName());
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			actualField.setAccessible(true); // 设置些属性是可以访问的
			expectField.setAccessible(true); // 设置些属性是可以访问的
			//获取属性值
			Object actualValue = actualField.get(actualObj);
			Object expectValue = expectField.get(expectObj);
//			System.out.println("actual("+actualField.getName()+":"+actualValue+")"+" expect("+expectField.getName()+":"+expectValue+")");
			if (!areEquals(actualValue,expectValue)){
				String msg = "\nactual("+actualField.getName()+":"+actualValue+")"+" expect("+expectField.getName()+":"+expectValue+")";
				throw new AssertException(AssertException.ARE_NOT_EQUAL, msg);
			}
		}
		return true;
	}
}
