package org.mingy.jmud.util;

/**
 * 变量相关的工具类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Variables {

	/**
	 * 变量值转换成字符串。
	 * 
	 * @param val
	 *            变量值
	 * @return 字符串，对象为null时返回""
	 */
	public static String toString(Object val) {
		return val != null ? val.toString() : "";
	}

	/**
	 * 变量值转换成双精度浮点数。
	 * 
	 * @param val
	 *            变量值
	 * @return 双精度浮点数，对象为null或格式错误时返回null
	 */
	public static Double toDouble(Object val) {
		try {
			if (val == null)
				return null;
			else if (val instanceof Double)
				return (Double) val;
			else if (val instanceof Float)
				return ((Float) val).doubleValue();
			else if (val instanceof Long)
				return ((Long) val).doubleValue();
			else if (val instanceof Integer)
				return ((Integer) val).doubleValue();
			else
				return Double.parseDouble(val.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 变量值转换成单精度浮点数。
	 * 
	 * @param val
	 *            变量值
	 * @return 单精度浮点数，对象为null或格式错误时返回null
	 */
	public static Float toFloat(Object val) {
		try {
			if (val == null)
				return null;
			else if (val instanceof Double)
				return ((Double) val).floatValue();
			else if (val instanceof Float)
				return (Float) val;
			else if (val instanceof Long)
				return ((Long) val).floatValue();
			else if (val instanceof Integer)
				return ((Integer) val).floatValue();
			else
				return Float.parseFloat(val.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 变量值转换成长整型。
	 * 
	 * @param val
	 *            变量值
	 * @return 长整型，对象为null或格式错误时返回null
	 */
	public static Long toLong(Object val) {
		try {
			if (val == null)
				return null;
			else if (val instanceof Double)
				return ((Double) val).longValue();
			else if (val instanceof Float)
				return ((Float) val).longValue();
			else if (val instanceof Long)
				return (Long) val;
			else if (val instanceof Integer)
				return ((Integer) val).longValue();
			else
				return Long.parseLong(val.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 变量值转换成整型。
	 * 
	 * @param val
	 *            变量值
	 * @return 整型，对象为null或格式错误时返回null
	 */
	public static Integer toInt(Object val) {
		try {
			if (val == null)
				return null;
			else if (val instanceof Double)
				return ((Double) val).intValue();
			else if (val instanceof Float)
				return ((Float) val).intValue();
			else if (val instanceof Long)
				return ((Long) val).intValue();
			else if (val instanceof Integer)
				return (Integer) val;
			else
				return Integer.parseInt(val.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 将变量值转换成JS的变量。
	 * <p>
	 * <li>null -&gt; null
	 * <li>10 -&gt; 10
	 * <li>"10" -&gt; '10'
	 * <li>"abc" -&gt; 'abc'
	 * <li>"a'bc" -&gt; 'a\'bc'
	 * <li>"a\bc" -&gt; 'a\\bc'
	 * </p>
	 * 
	 * @param val
	 *            变量值
	 * @return JS变量
	 */
	public static String toJSvar(Object val) {
		if (val == null)
			return "null";
		else if (val instanceof Number)
			return val.toString();
		String s = val.toString();
		s = s.replace("\\", "\\\\");
		s = s.replace("'", "\\'");
		return "'" + s + "'";
	}
}
