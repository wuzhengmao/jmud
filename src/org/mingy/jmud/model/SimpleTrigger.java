package org.mingy.jmud.model;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.mingy.jmud.model.Triggers.Line;

/**
 * 触发器。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SimpleTrigger extends Trigger {

	/** 编译后的模板 */
	private Pattern[] patterns;

	/**
	 * 构造器。
	 * 
	 * @param regex
	 *            正则表达式
	 * @param execution
	 *            执行逻辑
	 */
	public SimpleTrigger(String regex, Execution execution) {
		super(execution);
		patterns = new Pattern[] { Pattern.compile(regex) };
	}

	/**
	 * 构造器。
	 * 
	 * @param regexes
	 *            多个正则表达式
	 * @param execution
	 *            执行逻辑
	 */
	public SimpleTrigger(String[] regexes, Execution execution) {
		super(execution);
		patterns = new Pattern[regexes.length];
		for (int i = 0; i < regexes.length; i++)
			patterns[i] = Pattern.compile(regexes[i]);
	}

	@Override
	public int requiresLineCount() {
		return patterns.length;
	}

	@Override
	public String[] match(Line line, int start) {
		List<String> result = match(line, start, patterns[0]);
		if (result == null)
			return null;
		for (int i = 1; i < patterns.length; i++) {
			line = line.getNext();
			List<String> r = match(line, 0, patterns[i]);
			if (r == null)
				return null;
			result.set(0, result.get(0) + r.get(0));
			for (int j = 1; j < r.size(); j++)
				result.add(r.get(j));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("matches: " + result.get(0));
			for (int i = 1; i < result.size(); i++)
				logger.debug("args[" + i + "] = " + result.get(i));
		}
		return result.toArray(new String[result.size()]);
	}

	@Override
	public String toString() {
		return "SimpleTrigger [regex=" + Arrays.toString(patterns)
				+ ", execution=" + getExecution() + "]";
	}
}
