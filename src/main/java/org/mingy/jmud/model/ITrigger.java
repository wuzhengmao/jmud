package org.mingy.jmud.model;

import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.TriggerHandler.Line;

/**
 * 触发器接口。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public interface ITrigger {

	/**
	 * 触发器要求的文本行数。
	 * 
	 * @return 必须大于0，表示必须有几行
	 */
	int requiresLineCount();

	/**
	 * 匹配一行文本。
	 * 
	 * @param line
	 *            一行文本
	 * @param start
	 *            匹配的起始位置
	 * @return 匹配成功的参数，匹配成功而无参数时返回空的数组，未匹配成功时返回null
	 */
	String[] match(Line line, int start);

	/**
	 * 执行一系列指令。
	 * 
	 * @param client
	 *            MUD客户端
	 * @param args
	 *            匹配成功的参数，始终不为null
	 */
	void execute(IMudClient client, String[] args);

	/**
	 * 返回触发器是否启用。
	 * 
	 * @return true为启用
	 */
	boolean isEnabled();

	/**
	 * 设置触发器是否启用。
	 * 
	 * @param enabled
	 *            true为启用
	 */
	void setEnabled(boolean enabled);
}
