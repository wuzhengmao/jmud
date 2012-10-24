package org.mingy.jmud.model;

import org.mingy.jmud.model.Triggers.Line;

/**
 * 触发器的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Trigger {

	/** 执行逻辑 */
	private IExecution execution;

	/**
	 * 构造器。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	Trigger(IExecution execution) {
		this.execution = execution;
	}

	/**
	 * 触发器要求的文本行数。
	 * 
	 * @return 必须大于0，表示必须有几行
	 */
	public int requiresLineCount() {
		return 1;
	}

	/**
	 * 匹配一行文本。
	 * 
	 * @param line
	 *            一行文本
	 * @param start
	 *            匹配的起始位置
	 * @return 匹配成功的参数，未匹配成功时返回null
	 */
	public abstract String[] match(Line line, int start);

	/**
	 * 返回执行逻辑。
	 * 
	 * @return 执行逻辑
	 */
	public IExecution getExecution() {
		return execution;
	}

	/**
	 * 设置执行逻辑。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	public void setExecution(IExecution execution) {
		this.execution = execution;
	}
}
