package org.mingy.jmud.model;

/**
 * 移除变量监听的指令。
 * <p>
 * Usage: #unwatch [module]var id
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class UnwatchCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length != 2)
			return false;
		Object[] r = getScopeByPath(scope, items[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String var = (String) r[1];
		if (!SetCommand.CHECK.matcher(var).matches())
			return false;
		target.removeWatcher(var, items[1]);
		return true;
	}
}
