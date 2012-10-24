package org.mingy.jmud.model;

/**
 * IF语句。
 * <p>
 * Usage: #if expression {script if true} [{script if false}]
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class IfCommand extends Command {

	private Script script1, script2;

	@Override
	protected void prepare(String header, String[] items, String[] args) {
		if (items.length > 1)
			script1 = new Script(items[1]);
		if (items.length > 2)
			script2 = new Script(items[2]);
	}

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length < 2 || items.length > 3)
			return false;
		Object val = scope.calcExpression(items[0]);
		if (!(val == null || (val instanceof Boolean && !(Boolean) val))) {
			if (logger.isTraceEnabled()) {
				logger.trace("execute true statement");
			}
			script1.execute(scope, args);
		} else if (script2 != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("execute false statement");
			}
			script2.execute(scope, args);
		}
		return true;
	}
}
