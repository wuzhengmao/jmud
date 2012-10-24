package org.mingy.jmud.model;

/**
 * 中止脚本的指令。
 * <p>
 * Usage: #return
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ReturnCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length > 0)
			return false;
		throw new InterruptExecutionException();
	}
}
