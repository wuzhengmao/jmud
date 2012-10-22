package org.mingy.jmud.model;

/**
 * 跳过一次循环的指令。
 * <p>
 * Usage: #skip
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class SkipCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length > 0)
			return false;
		throw new SkipOnceException();
	}
}
