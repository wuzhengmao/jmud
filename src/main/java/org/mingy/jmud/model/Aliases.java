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

	/** 上级模块的别名 */
	private Aliases parent;

	/**
	 * 构造器。
	 */
	public Aliases() {
		ALL = new HashMap<String, Alias>();
	}

	/**
	 * 构造器。
	 * 
	 * @param parent
	 *            上级模块的别名
	 */
	public Aliases(Aliases parent) {
		this();
		this.parent = parent;
	}

	/**
	 * 取得一个定义的别名。
	 * 
	 * @param name
	 *            别名
	 * @return 定义的别名，未定义时返回null
	 */
	public Alias get(String name) {
		Alias alias = ALL.get(name);
		if (alias == null && parent != null)
			alias = parent.get(name);
		return alias;
	}

	/**
	 * 设置一个别名。
	 * 
	 * @param name
	 *            别名
	 * @param script
	 *            脚本
	 * @return 新增或修改后的别名
	 */
	public Alias set(String name, String script) {
		Alias alias = ALL.get(name);
		if (alias != null) {
			alias.setScript(script);
		} else {
			alias = new Alias(name, script);
			ALL.put(name, alias);
		}
		return alias;
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
