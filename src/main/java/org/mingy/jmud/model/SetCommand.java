package org.mingy.jmud.model;

import java.util.regex.Pattern;

/**
 * 赋值的指令。
 * <p>
 * Usage: #set var [expression]
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SetCommand extends Command {

	private static final Pattern CHECK = Pattern
			.compile("^[A-Za-z][A-Za-z0-9_]*$");

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length == 0 || items.length > 2)
			return false;
		Object[] r = getScopeByPath(scope, items[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String var = (String) r[1];
		if (!CHECK.matcher(var).matches())
			return false;
		if (items.length == 1) {
			target.removeVariable(var);
		} else {
			target.setVariable(var, scope.calcExpression(items[1]));
		}
		return true;
	}
}
