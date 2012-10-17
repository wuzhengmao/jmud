package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;

/**
 * 快捷键定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ShortKeys {

	/** 默认的快捷键 */
	public static final Map<Integer, ShortKey> DEFAULT;

	/** 所有的快捷键 */
	private final Map<Integer, ShortKey> ALL;

	static {
		DEFAULT = new HashMap<Integer, ShortKey>();
		add(SWT.KEYPAD_1, "southwest", DEFAULT);
		add(SWT.KEYPAD_2, "south", DEFAULT);
		add(SWT.KEYPAD_3, "southeast", DEFAULT);
		add(SWT.KEYPAD_4, "west", DEFAULT);
		add(SWT.KEYPAD_5, "look", DEFAULT);
		add(SWT.KEYPAD_6, "east", DEFAULT);
		add(SWT.KEYPAD_7, "northwest", DEFAULT);
		add(SWT.KEYPAD_8, "north", DEFAULT);
		add(SWT.KEYPAD_9, "northeast", DEFAULT);
	}

	/**
	 * 构造器。
	 */
	public ShortKeys() {
		ALL = new HashMap<Integer, ShortKey>();
		ALL.putAll(DEFAULT);
	}

	private static void add(int key, String command,
			Map<Integer, ShortKey> target) {
		ShortKey shortKey = new ShortKey(key, command);
		target.put(key, shortKey);
	}

	/**
	 * 取得一个定义的快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @return 定义的快捷键，未定义时返回null
	 */
	public ShortKey get(int key) {
		return ALL.get(key);
	}

	/**
	 * 添加一个快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @param command
	 *            命令行
	 */
	public void add(int key, String command) {
		add(key, command, ALL);
	}

	/**
	 * 移除一个快捷键。
	 * 
	 * @param key
	 *            组合键
	 * @return 移除的快捷键，未定义时返回null
	 */
	public ShortKey remove(int key) {
		return ALL.remove(key);
	}
}
