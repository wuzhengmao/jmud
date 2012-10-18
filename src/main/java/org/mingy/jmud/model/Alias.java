package org.mingy.jmud.model;

/**
 * 别名。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class Alias {

	/** 别名 */
	private String name;
	/** 脚本 */
	private String script;

	/**
	 * 构造器。
	 * 
	 * @param name
	 *            别名
	 * @param script
	 *            脚本
	 */
	public Alias(String name, String script) {
		this.name = name;
		this.script = script;
	}

	/**
	 * 返回别名。
	 * 
	 * @return 别名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 返回脚本
	 * 
	 * @return 脚本
	 */
	public String getScript() {
		return script;
	}
}
