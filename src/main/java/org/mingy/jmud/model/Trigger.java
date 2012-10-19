package org.mingy.jmud.model;

import org.mingy.jmud.model.Triggers.Line;

/**
 * 触发器的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Trigger {

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
	 * 执行一系列指令。
	 * 
	 * @param scope
	 *            上下文
	 * @param args
	 *            匹配成功的参数，始终不为null
	 */
	public abstract void execute(IScope scope, String[] args);
}
