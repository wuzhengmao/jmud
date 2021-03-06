package org.mingy.jmud.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.model.Triggers.Line;
import org.mingy.jmud.util.Strings;

/**
 * 触发器的抽象类。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public abstract class Trigger {

	/** 日志 */
	protected static final Log logger = LogFactory.getLog(Trigger.class);

	/** 执行逻辑 */
	private Execution execution;

	/**
	 * 构造器。
	 * 
	 * @param execution
	 *            执行逻辑
	 */
	public Trigger(Execution execution) {
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
	 * 用正则表达式来匹配一行文本。
	 * 
	 * @param line
	 *            一行文本
	 * @param start
	 *            匹配的起始位置
	 * @param pattern
	 *            正则表达式
	 * @return 匹配成功的参数，未匹配成功时返回null
	 */
	protected static List<String> match(Line line, int start, Pattern pattern) {
		String text = line.getText();
		if (Strings.isEmpty(text))
			return null;
		String regex = pattern.pattern();
		if (regex.charAt(0) == '^' && start > 0)
			return null;
		if (regex.charAt(regex.length() - 1) == '$'
				&& text.charAt(text.length() - 1) != '\n')
			return null;
		text = text.substring(start);
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			List<String> result = new ArrayList<String>();
			result.add(text);
			for (int i = 1; i <= m.groupCount(); i++)
				result.add(m.group(i));
			return result;
		}
		return null;
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
}
