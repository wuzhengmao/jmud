package org.mingy.jmud.model;

/**
 * While语句。
 * <p>
 * Usage: #while expression {script}
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class WhileCommand extends Command {

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
		while (true) {
			Object val = scope.calcExpression(items[0]);
			if (!(val == null || (val instanceof Boolean && !(Boolean) val))) {
				if (logger.isTraceEnabled()) {
					logger.trace("do loop");
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
			} else {
				if (logger.isTraceEnabled()) {
					logger.trace("end loop");
				}
				break;
			}
		}
		return true;
	}
}
