package org.mingy.jmud.model;

/**
 * 添加变量监听的指令。
 * <p>
 * Usage: #watch [module]var [id] [script]
 * </p>
 * 
 * @author Mingy
 * @since 1.0.0
 */
public class WatchCommand extends Command {

	@Override
	protected boolean execute(IScope scope, String header, String[] items,
			String[] args) throws Exception {
		if (items.length < 2 || items.length > 3)
			return false;
		Object[] r = getScopeByPath(scope, items[0]);
		if (r == null)
			return false;
		IScope target = (IScope) r[0];
		String var = (String) r[1];
		if (!SetCommand.CHECK.matcher(var).matches())
			return false;
		if (items.length == 2) {
			target.addWatcher(var, items[1]);
		} else {
			target.addWatcher(var, items[1], items[2]);
		}
		return true;
	}
}
