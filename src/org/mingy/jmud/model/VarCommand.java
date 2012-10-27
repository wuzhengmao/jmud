package org.mingy.jmud.model;

import java.util.regex.Pattern;

/**
 * 临时变量的赋值的指令。
 * <p>
 * Usage: #var var [expression]
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class VarCommand extends Command {

	private static final Pattern CHECK = Pattern
			.compile("^[A-Za-z][A-Za-z0-9_]*$");

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length == 0 || items.length > 2)
			return false;
		if (!CHECK.matcher(items[0]).matches())
			return false;
		if (items.length == 1) {
			scope.removeVariable(items[0], true);
		} else {
			scope.setVariable(items[0], scope.calcExpression(items[1]), true);
		}
		return true;
	}
}
