package org.mingy.jmud.model;

import org.mingy.jmud.util.Strings;
import org.mingy.jmud.util.Variables;

/**
 * 循环语句。
 * <p>
 * Usage: #loop expression {script}<br>
 * 可简写为：#10 {script}
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class LoopCommand extends Command {

	private Script script;

	@Override
	protected void prepare(String header, String[] items, String[] args) {
		if (items.length > 1)
			script = new Script(items[1]);
	}

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length != 2)
			return false;
		Object val = scope.calcExpression(items[0]);
		if (!Strings.isNumber(String.valueOf(val), true, false))
			return false;
		int times = Variables.toInt(val);
		for (int i = 0; i < times; i++) {
			if (logger.isTraceEnabled()) {
				logger.trace("loop: " + i);
			}
			try {
				script.execute(scope, args);
			} catch (SkipOnceException e) {
				if (logger.isTraceEnabled()) {
					logger.trace("continue loop");
				}
			} catch (BreakLoopException e) {
				if (logger.isTraceEnabled()) {
					logger.trace("break loop");
				}
				break;
			}
		}
		return true;
	}
}
