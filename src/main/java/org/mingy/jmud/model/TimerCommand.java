package org.mingy.jmud.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mingy.jmud.util.Variables;

/**
 * 定时器控制指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class TimerCommand extends Command {

	private static final Pattern PATTERN = Pattern
			.compile("^(\\d+)\\s*((?:|h|m|s|ms))$");

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length == 0 || items.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, items[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String name = (String) r[1];
		if ("#ti".equals(header) || "#timer".equals(header)) {
			if (items.length == 1) {
				target.removeTimer(name);
			} else {
				target.setTimer(name, items[1]);
			}
			return true;
		} else if ("#ts".equals(header) || "#tset".equals(header)) {
			if (items.length == 1) {
				return target.switchTimer(name) != null;
			} else {
				Integer tick = null;
				Matcher m = PATTERN.matcher(items[1]);
				if (m.find()) {
					tick = Integer.parseInt(m.group(1));
					String unit = m.group(2);
					if ("s".equals(unit))
						tick *= 1000;
					else if ("m".equals(unit))
						tick *= 1000 * 60;
					else if ("h".equals(unit))
						tick *= 1000 * 60 * 60;
				}
				if (tick == null)
					tick = Variables.toInt(scope.calcExpression(items[1]));
				if (tick == null)
					return false;
				return target.resetTimer(name, tick) != null;
			}
		}
		return false;
	}
}
