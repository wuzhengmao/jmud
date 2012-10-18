package org.mingy.jmud.model;

import java.util.regex.Pattern;

import javax.script.ScriptException;

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
	public boolean execute(Context context) {
		if (args.length != 2)
			return false;
		String var = args[0];
		if (!CHECK.matcher(var).matches())
			return false;
		String expression = Commands.replaceVariables(context, args[1]);
		try {
			context.JS.put(var, context.JS.eval(expression));
		} catch (ScriptException e) {
			if (logger.isErrorEnabled()) {
				logger.error("error on run script", e);
			}
			return false;
		}
		return true;
	}
}
