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

	/** 执行逻辑 */
	private Execution execution;

	/**
	 * 构造器。
	 * 
	 * @param name
	 *            别名
	 * @param execution
	 *            执行逻辑
	 */
	Alias(String name, Execution execution) {
		this.name = name;
		this.execution = execution;
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
	 * 返回执行逻辑。
	 * 
	 * @return 执行逻辑
	 */
	public Execution getExecution() {
		return execution;
	}

	/**
	 * 设置执行逻辑。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	public void setExecution(Execution execution) {
		this.execution = execution;
	}

	@Override
	public String toString() {
		return "Alias [name=" + name + ", execution=" + execution + "]";
	}
}
