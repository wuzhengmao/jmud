package org.mingy.jmud.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关的工具类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Strings {

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	private static Pattern NUMBER = Pattern.compile("^([+|-]?)\\d*(.?\\d+)$");

	public static boolean isNumber(String str, boolean allowDecimal,
			boolean allowSign) {
		if (isEmpty(str))
			return false;
		Matcher m = NUMBER.matcher(str);
		if (!m.find())
			return false;
		if (!allowDecimal && m.group(2).charAt(0) == '.')
			return false;
		if (!allowSign && !isEmpty(m.group(1)))
			return false;
		return true;
	}

	public static Charset toCharset(String str) {
		try {
			return Charset.forName(str);
		} catch (UnsupportedCharsetException e) {
			return null;
		}
	}
}
