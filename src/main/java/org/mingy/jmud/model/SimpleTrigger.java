package org.mingy.jmud.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mingy.jmud.model.Triggers.Line;

/**
 * 触发器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SimpleTrigger extends Trigger {

	/** 日志 */
	private static final Log logger = LogFactory.getLog(Trigger.class);

	/** 正则表达式 */
	private String regex;

	/** 编译后的模板 */
	private Pattern pattern;

	/**
	 * 构造器。
	 * 
	 * @param regex
	 *            正则表达式
	 * @param execution
	 *            执行逻辑
	 */
	SimpleTrigger(String regex, IExecution execution) {
		super(execution);
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public String[] match(Line line, int start) {
		if (regex.charAt(0) == '^' && start > 0)
			return null;
		if (regex.charAt(regex.length() - 1) == '$'
				&& !line.getText().endsWith("\n"))
			return null;
		Matcher m = pattern.matcher(line.getText().substring(start));
		if (m.find()) {
			String[] result = new String[m.groupCount() + 1];
			result[0] = m.group();
			for (int i = 1; i < result.length; i++)
				result[i] = m.group(i);
			if (logger.isDebugEnabled()) {
				logger.debug("matches: " + result[0]);
				for (int i = 1; i < result.length; i++)
					logger.debug("args[" + i + "] = " + result[i]);
			}
			return result;
		}
		return null;
	}

	/**
	 * 取得编译后的模板。
	 * 
	 * @return 匹配模板
	 */
	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public String toString() {
		return "SimpleTrigger [regex=" + regex + ", execution="
				+ getExecution() + "]";
	}
}
