package org.mingy.jmud.model;

import org.mingy.jmud.client.IMudClient;
import org.mingy.jmud.model.TriggerHandler.Line;

/**
 * 触发器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SimpleTrigger extends Trigger {

	/** 匹配模板 */
	private String pattern;

	/** 触发后执行的指令 */
	private String command;

	/**
	 * 构造器。
	 * 
	 * @param pattern
	 *            匹配模板
	 * @param command
	 *            触发后执行的指令
	 */
	public SimpleTrigger(String pattern, String command) {
		super();
		this.pattern = pattern;
		this.command = command;
	}

	public String[] match(Line line, int start) {
		if (pattern.charAt(0) == '^' && start > 0)
			return null;
		if (pattern.charAt(pattern.length() - 1) == '$'
				&& !line.getText().endsWith("\n"))
			return null;
		return null;
	}

	public void execute(IMudClient client, String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * 用定义的模板来匹配字符串。
	 * 
	 * @param line
	 *            字符串
	 * @return 成功后返回匹配值，否则返回null
	 */
	public String[] match(String line) {
		return null;
	}

	/**
	 * 取得触发器的匹配模板。
	 * 
	 * @return 匹配模板
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * 取得触发后执行的指令。
	 * 
	 * @return 触发后执行的指令
	 */
	public String getCommand() {
		return command;
	}
}
