package org.mingy.jmud.model;

import org.mingy.jmud.util.Variables;

/**
 * 等待指令。
 * <p>
 * Usage: #wait milliseconds
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class WaitCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length != 1)
			return false;
		Integer time = null;
		try {
			time = Integer.parseInt(items[0]);
		} catch (Exception e) {
			time = Variables.toInt(scope.calcExpression(items[0]));
		}
		if (time == null || time < 0)
			return false;
		if (time > 0) {
			long n = 0;
			if (logger.isTraceEnabled()) {
				logger.trace("waiting " + time + "ms ...");
				n = System.currentTimeMillis();
			}
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// ignore
			}
			if (logger.isTraceEnabled()) {
				n = System.currentTimeMillis() - n;
				logger.trace("wait over, cost " + n + "ms");
			}
		}
		return true;
	}
}
