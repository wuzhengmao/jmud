package org.mingy.jmud.model;

import java.util.regex.Pattern;

/**
 * 赋值的指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SetCommand extends Command {

	private static final Pattern CHECK = Pattern
			.compile("^[A-Za-z][A-Za-z0-9_]*$");

	@Override
	public boolean execute(IScope scope) {
		if (args.length == 0 || args.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, args[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String var = (String) r[1];
		if (!CHECK.matcher(var).matches())
			return false;
		if (args.length == 1) {
			target.removeVariable(var);
		} else {
			try {
				target.setVariable(var, scope.calcExpression(args[1]));
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("error on run script", e);
				}
				return false;
			}
		}
		return true;
	}
}
