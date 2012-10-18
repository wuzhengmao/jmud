package org.mingy.jmud.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 别名的定义。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Aliases {

	/** 所有的别名 */
	private final Map<String, Alias> ALL;

	/**
	 * 构造器。
	 */
	public Aliases() {
		ALL = new HashMap<String, Alias>();
	}

	/**
	 * 取得一个定义的别名。
	 * 
	 * @param name
	 *            别名
	 * @return 定义的别名，未定义时返回null
	 */
	public Alias get(String name) {
		return ALL.get(name);
	}

	/**
	 * 添加一个别名。
	 * 
	 * @param name
	 *            别名
	 * @param script
	 *            脚本
	 */
	public void add(String name, String script) {
		ALL.put(name, new Alias(name, script));
	}

	/**
	 * 移除一个别名。
	 * 
	 * @param name
	 *            别名
	 * @return 移除的别名，未定义时返回null
	 */
	public Alias remove(String name) {
		return ALL.remove(name);
	}
}
