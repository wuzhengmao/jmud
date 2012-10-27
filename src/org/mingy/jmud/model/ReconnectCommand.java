package org.mingy.jmud.model;

/**
 * 重新连线指令。
 * <p>
 * Usage: #reconnect
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class ReconnectCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length > 0)
			return false;
		scope.reconnect();
		return true;
	}
}
