package org.mingy.jmud.model;

/**
 * 断开连接指令。
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class DisconnectCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length > 0)
			return false;
		scope.disconnect();
		return true;
	}
}
