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
	public boolean execute(IScope scope) throws Exception {
		if (args.length == 0 || args.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, args[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String name = (String) r[1];
		if ("#ti".equals(header) || "#timer".equals(header)) {
			if (args.length == 1) {
				target.removeTimer(name);
			} else {
				target.setTimer(name, args[1]);
			}
			return true;
		} else if ("#ts".equals(header) || "#tset".equals(header)) {
			if (args.length == 1) {
				return target.switchTimer(name) != null;
			} else {
				Integer tick = null;
				Matcher m = PATTERN.matcher(args[1]);
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
					tick = Variables.toInt(scope.calcExpression(args[1]));
				if (tick == null)
					return false;
				return target.resetTimer(name, tick) != null;
			}
		}
		return false;
	}
}
